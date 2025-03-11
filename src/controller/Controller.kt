package controller

import model.Data
import model.Map
import model.Model
import model.objects.base.Inventory
import model.objects.base.entities.Enemy
import model.objects.base.entities.Hero
import model.objects.base.item.Armor
import model.objects.base.item.Consumable
import model.objects.base.item.Item
import model.objects.base.item.Weapon
import model.objects.world.Directions
import model.objects.world.ItemNotThereException
import model.objects.world.RoomNotThereException
import view.View
import java.awt.Font
import java.awt.event.ActionEvent
import javax.swing.AbstractAction
import javax.swing.JTextField
import javax.swing.JTextPane
import javax.swing.KeyStroke
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants
import kotlin.system.exitProcess

/**
 * extension function to the JTextPane (class of text output) to simplify appending of text
 *
 * default is bold printing to distinguish game output from repeating of player commands
 */
fun JTextPane.respond(message: String?, bold: Boolean = true) {
    if (bold) {
        val attributes = SimpleAttributeSet()
        StyleConstants.setBold(attributes, true)
        styledDocument.insertString(document.length, "$message\n", attributes)
    } else {
        styledDocument.insertString(document.length, "$message\n", null)
    }
}

class Controller {
    // reference to model, view and combat handling class
    private var model = Model("test")
    private val view = View(model)
    private var combat: Combat? = null

    // variables for input history
    private val history: MutableList<String> = mutableListOf("")
    private var historyIndex = 0

    // mappings for direction strings to internally used directions
    // slightly janky workaround for 2d array directions to cardinal directions
    private val movement = mapOf(
        "north" to Directions.WEST,
        "east" to Directions.SOUTH,
        "south" to Directions.EAST,
        "west" to Directions.NORTH
    )

    init {
        view.menuBar.exit.addActionListener { exitProcess(0) }
        // listener for enter
        // history adding, input repeating, basic handling
        view.content.input.addActionListener {
            respond(view.content.input.text, false)
            history.add(1, view.content.input.text)
            parseInput(view.content.input.text)
            view.content.input.text = ""
            historyIndex = 0
        }
        // arrow up and down handling for cycling through history
        view.content.input.getInputMap(JTextField.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("UP"), "arrowUp")
        view.content.input.getInputMap(JTextField.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("DOWN"), "arrowDown")
        view.content.input.actionMap.put("arrowUp", object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent) {
                if (history.size - 1 > historyIndex) {
                    historyIndex++
                    view.content.input.text = history[historyIndex]
                }
            }
        })
        view.content.input.actionMap.put("arrowDown", object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent) {
                if (historyIndex > 0) {
                    historyIndex--
                    view.content.input.text = history[historyIndex]
                }
            }
        })
        // loading initial data from model to view
        updateMap()
        updateInfo()
        respond("Welcome to Tower of Text")
    }

    /**
     * function to scroll to bottom of output component (doesn't really work for some reason)
     */
    private fun scrollToBottom() {
        view.content.scroll.verticalScrollBar.value = view.content.scroll.verticalScrollBar.maximum
    }

    /**
     * wrapper function that automatically scrolls to new text (jpanel extension function has no access to controller vars and functions)
     */
    private fun respond(message: String?, bold: Boolean = true) {
        view.content.output.respond(message, bold)
        scrollToBottom()
    }

    /**
     * adds o for hero room and adjacent x for adjacent rooms to map
     * works because hero can only move one room at a time so former hero locations get overwritten
     * results in gradual map progression
     */
    private fun updateMap() {
        view.content.sidebar.map.setValueAt("o", model.hero.room.coords.x, model.hero.room.coords.y)

        for (room in model.map.neighbours(model.hero.room.coords)) {
            if (room == null) {
                continue
            }
            view.content.sidebar.map.setValueAt("x", room.coords.x, room.coords.y)
        }
    }

    /**
     * clears map to hero position and adjacent rooms
     */
    private fun clearMap() {
        for (i in 0..<model.mapSize.x) {
            for (j in 0..<model.mapSize.y) {
                view.content.sidebar.map.setValueAt(" ", i, j)
            }
        }
        updateMap()
    }

    /**
     * updates info in the sidebar
     */
    private fun updateInfo() {
        // approach is to create a list and fill it with infos to create a 2d array
        val infoData: MutableList<Array<String>> = mutableListOf()

        // general info
        infoData.add(arrayOf("floor", "", model.floor.toString()))
        infoData.add(arrayOf("health", "", model.hero.health.toString()))
        infoData.add(arrayOf("gold", "", model.hero.coins.toString()))
        infoData.add(Array(3) { "" })

        // weapons
        infoData.add(arrayOf("WEAPONS", "", ""))
        infoData.add(arrayOf("id", "name", "dmg"))

        // equipped weapon
        infoData.add(arrayOf("x", model.hero.weapon.name, model.hero.weapon.damage.toString()))

        // weapons from inventory
        model.hero.inventory.filterIsInstance<Weapon>().forEachIndexed { id, weapon ->
            infoData.add(arrayOf(id.toString(), weapon.name, weapon.damage.toString()))
        }

        infoData.add(Array(3) { "" })

        // armors
        infoData.add(arrayOf("ARMOR", "", ""))
        infoData.add(arrayOf("id", "name", "abs"))

        // equipped armor
        infoData.add(
            arrayOf(
                "x",
                model.hero.armor.name,
                model.hero.armor.absorption.toString(),
            )
        )

        // armors in inventory
        model.hero.inventory.filterIsInstance<Armor>().forEachIndexed { id, armor ->
            infoData.add(
                arrayOf(
                    id.toString(),
                    armor.name,
                    armor.absorption.toString(),
                )
            )
        }

        infoData.add(Array(3) { "" })

        // consumables (called items here because of space reasons)
        infoData.add(arrayOf("ITEMS", "", ""))
        infoData.add(arrayOf("id", "name", "effect"))

        model.hero.inventory.filterIsInstance<Consumable>().forEachIndexed { id, consumable ->
            infoData.add(arrayOf(id.toString(), consumable.name, consumable.description))
        }

        infoData.add(Array(3) { "" })

        // conversion to array (data in table used for displaying info can be set using 2d array)
        model.infoModel.setDataVector(infoData.toTypedArray(), arrayOf("0", "1", "2"))

        // adjusting of sidebar size to fit
        view.content.sidebar.informationColumnWidths.forEachIndexed { column, widthPercentage ->
            view.content.sidebar.information.columnModel.getColumn(column).preferredWidth =
                (view.content.sidebar.information.width * widthPercentage).toInt()
        }
    }

    /**
     * giant function to parse input and act accordingly
     */
    private fun parseInput(input: String) {
        // splitting into tokens
        val splitInput = input.split(" ")

        // throw error when no command is specified
        if (splitInput.isEmpty()) {
            respond("no command specified!")
            return
        }

        // when combat is active, use combat state machine instead of normal input parser
        if (combat != null) {
            // Process combat commands if a combat session is active.
            when (combat!!.combatParse(splitInput)) {
                0 -> {
                    // Case 0: Combat action executed, but no terminal event occurred.
                    // Update the player info and map, then continue the combat.
                    updateInfo()
                    updateMap()
                    return
                }

                1 -> {
                    // Case 1: The enemy has been defeated.
                    // End the combat session by setting combat to null, update info and map.
                    combat = null
                    updateInfo()
                    updateMap()
                    return
                }

                2 -> {
                    // Case 2: The hero has died during combat.
                    // Trigger the heroDeath() routine, update info and map, and end the combat session.
                    heroDeath()
                    updateInfo()
                    updateMap()
                    combat = null
                    return
                }

                3 -> {
                    // Case 3: The hero successfully escaped combat.
                    // Move the hero back to their last room, update info and map, and end the combat session.
                    model.hero.room = model.hero.lastRoom
                    updateInfo()
                    updateMap()
                    combat = null
                    return
                }
            }
            return
        }


        // converting to lowercase as combat handling sometimes requires case sensitivity
        splitInput.forEach { it.lowercase() }

        // actual input handling
        // repeating parts are explained only the first time they're used
        when (splitInput[0]) {
            // movement
            "move" -> {
                // assure proper length
                if (splitInput.size < 2) {
                    respond("usage: move [direction]")
                    return
                }

                // check if direction exists
                if (splitInput[1] !in movement.keys) {
                    respond("this direction doesn't exist!")
                    return
                }

                // execution of movement (try-catch because move method throws exception when there's no room in that direction)
                try {
                    model.map.move(movement[splitInput[1]] as Int, model.hero)
                    updateMap()
                    respond("moved to the ${splitInput[1]}")

                    // information about entered room
                    if (model.hero.room.entities.filterIsInstance<Enemy>().isNotEmpty()) {
                        for (i in 0..<model.hero.room.entities.size) {
                            respond(model.hero.room.entities[i].name)
                        }
                        combat = Combat(model.hero.room.entities.filterIsInstance<Enemy>()[0], model.hero, view)
                        respond("you entered combat with a ${model.hero.room.entities[0].name}")
                        respond("it intends to attack for ${model.hero.room.entities[0].weapon.damage} damage")
                    }

                    if (model.hero.room == model.map.shopRoom) {
                        respond("you find yourself in a shop")
                    }

                    if (model.hero.room in model.map.chestRooms) {
                        respond("the room you enter is empty except for a single chest")
                    }

                    if (model.hero.room == model.map.endRoom) {
                        respond("before you, you see a ladder leading up")
                    }
                } catch (e: RoomNotThereException) {
                    respond(e.message)
                }
            }

            // dropping items to room
            "drop" -> {
                if (splitInput.size < 3) {
                    respond("usage: drop [item class] [item id]")
                    return
                }

                // creating selection of specified item class
                val selection = createSelection(splitInput[1], model.hero.inventory)

                if (selection.isEmpty()) {
                    respond("there are no items of type ${splitInput[1]} in your inventory")
                }

                // use underlying hero method to drop item
                try {
                    model.hero.drop(selection[splitInput[2].toInt()])
                    updateInfo()
                    respond("dropped ${splitInput[1]} ${selection[splitInput[2].toInt()].name}")
                } catch (e: IndexOutOfBoundsException) {
                    respond("item id ${splitInput[2]} doesn't correspond to an item in your inventory")
                } catch (e: ItemNotThereException) {
                    respond(e.message)
                }
            }

            // equipping of item from inventory
            "equip" -> {
                if (splitInput.size < 3) {
                    respond("usage: equip [item class] [item id]")
                    return
                }

                // assure proper type
                if (splitInput[1] == "consumable") {
                    respond("can't equip a consumable")
                    return
                }

                val selection = createSelection(splitInput[1], model.hero.inventory)

                if (selection.isEmpty()) {
                    respond("there are no items of type ${splitInput[1]} in your inventory")
                }

                // use underlying hero method to equip item
                try {
                    model.hero.equip(selection[splitInput[2].toInt()])
                    updateInfo()
                    respond("equipped ${splitInput[1]} ${selection[splitInput[2].toInt()].name}")
                } catch (e: IndexOutOfBoundsException) {
                    respond("item id ${splitInput[2]} doesn't correspond to an item in your inventory")
                } catch (e: ItemNotThereException) {
                    respond(e.message)
                }
            }

            // unequipping equipped armor/weapon
            "unequip" -> {
                if (splitInput.size < 2) {
                    respond("usage: unequip [item class]")
                    return
                }

                if (splitInput[1] == "consumable") {
                    respond("can't unequip a consumable")
                    return
                }

                // unequip with underlying hero method according to specified item class
                when (splitInput[1]) {
                    "armor" -> {
                        respond("unequipped armor ${model.hero.armor.name}")
                        model.hero.unequip(splitInput[1])
                        updateInfo()
                    }

                    "weapon" -> {
                        respond("unequipped weapon ${model.hero.weapon.name}")
                        model.hero.unequip(splitInput[1])
                        updateInfo()
                    }

                    else -> {
                        respond("can't unequip item of type ${splitInput[1]}")
                    }
                }
            }

            // using of consumable
            "use" -> {
                if (splitInput.size < 2) {
                    respond("usage: use [consumable id]")
                }

                val selection = model.hero.inventory.filterIsInstance<Consumable>()

                // calling hero method to use consumable
                try {
                    model.hero.use(selection[splitInput[1].toInt()], model.hero)
                    updateInfo()
                    updateMap()
                } catch (e: IndexOutOfBoundsException) {
                    respond("no consumable with that index!")
                }
            }

            // picking up item from room
            "pickup" -> {
                if (splitInput.size < 3) {
                    respond("usage: pickup [item class] [item id]")
                    return
                }

                val selection = createSelection(splitInput[1], model.hero.room.inventory)

                if (selection.isEmpty()) {
                    respond("there are no items of type ${splitInput[1]} in the room")
                }

                // using hero pickup method
                try {
                    model.hero.pickup(selection[splitInput[2].toInt()])
                    updateInfo()
                    respond("picked up ${splitInput[1]} ${selection[splitInput[2].toInt()].name}")
                } catch (e: IndexOutOfBoundsException) {
                    respond("this item id doesn't correspond to an item in the current room")
                } catch (e: ItemNotThereException) {
                    respond(e.message)
                }
            }

            // method to get info about current room
            "inspect" -> {
                if (model.hero.room.inventory.export().isEmpty()) {
                    respond("there are no items in this room!")
                    return
                }

                // set font to monospace to ensure alignment when printing in a sort of pseudo-table
                val style = SimpleAttributeSet()
                StyleConstants.setFontFamily(style, Font.MONOSPACED)

                val doc = view.content.output.styledDocument

                // using inventory export method to get nicely readable information
                model.hero.room.inventory.export().forEach { block ->
                    // calculating max lengths of columns for each block (armor, weapon, consumable)
                    val maxLengths = Array(4) { 0 }
                    block.forEach { row ->
                        row.forEachIndexed { i, word ->
                            if (word.length + 2 > maxLengths[i]) {
                                maxLengths[i] = word.length + 2
                            }
                        }
                    }

                    // printing according to maximum lengths so everything looks nice and tidy
                    block.forEach { row ->
                        row.forEachIndexed { i, word ->
                            println(word.padEnd(maxLengths[i]))
                            doc.insertString(doc.length, word.padEnd(maxLengths[i]), style)
                        }

                        doc.insertString(doc.length, "\n", style)
                        scrollToBottom()
                    }
                }
            }

            // shop handling
            "shop" -> {
                // Create a temporary inventory for the shop containing all available items.
                val inv = Inventory(Data.weapons.size + Data.armors.size + Data.consumables.size)

                // Add weapons to the shop inventory with scaled damage values.
                for (weapon in Data.weapons) {
                    val tWeapon = weapon.copy()
                    tWeapon.damage = ((weapon.damage + 6) * model.map.scale).toInt()
                    inv.add(tWeapon)
                }

                // Add armors to the shop inventory with scaled absorption values.
                for (armor in Data.armors) {
                    val tArmor = armor.copy()
                    tArmor.absorption = ((armor.absorption + 3) * model.map.scale).toInt()
                    inv.add(tArmor)
                }

                // Add consumables to the shop inventory.
                for (consumable in Data.consumables) {
                    inv.add(consumable.copy())
                }

                // Ensure the hero is in a shop room before proceeding.
                if (model.hero.room != model.map.shopRoom) {
                    respond("You are not in a shop room")
                    return
                }

                when (splitInput[1]) {
                    "sell" -> {
                        // Selling an item from the hero's inventory.

                        if (splitInput.size < 4) {
                            // Not enough arguments provided, possibly missing item type or index.
                            return
                        }

                        // Select items of the given type from the hero's inventory.
                        val selection = createSelection(splitInput[2], model.hero.room.inventory)

                        if (selection.isEmpty()) {
                            respond("There are no items of type ${splitInput[2]} in your inventory")
                        }

                        try {
                            // Remove the selected item from the hero's inventory and add coins.
                            model.hero.inventory.remove(selection[splitInput[3].toInt()])
                            model.hero.coins += 15
                        } catch (e: IndexOutOfBoundsException) {
                            // Invalid item index provided.
                            respond("This item ID doesn't correspond to an item in your inventory")
                        } catch (e: ItemNotThereException) {
                            // Handle custom exception for missing item.
                            respond(e.message)
                        }
                    }

                    "buy" -> {
                        // Buying an item from the shop inventory.

                        if (splitInput.size < 4) {
                            // Not enough arguments provided, possibly missing item type or index.
                            return
                        }

                        // Select items of the given type from the shop inventory.
                        val selection = createSelection(splitInput[2], inv)

                        try {
                            if (model.hero.coins >= 150) {
                                // If the hero has enough coins, complete the purchase.
                                model.hero.inventory.add(selection[splitInput[3].toInt()])
                                model.hero.coins -= 150
                            } else {
                                // Not enough coins to make the purchase.
                                respond("You don't have enough coins left!")
                            }
                        } catch (e: IndexOutOfBoundsException) {
                            // Invalid item index provided.
                            respond("This item ID doesn't correspond to an item in the shop")
                        } catch (e: ItemNotThereException) {
                            // Handle custom exception for missing item.
                            respond(e.message)
                        }
                    }

                    "info" -> {
                        // Display shop inventory details in a formatted manner.

                        val style = SimpleAttributeSet()
                        StyleConstants.setFontFamily(style, Font.MONOSPACED)

                        val doc = view.content.output.styledDocument

                        inv.export().forEach { block ->
                            val maxLengths = Array(4) { 0 }

                            // Determine the maximum column width for alignment.
                            block.forEach { row ->
                                row.forEachIndexed { i, word ->
                                    if (word.length + 2 > maxLengths[i]) {
                                        maxLengths[i] = word.length + 2
                                    }
                                }
                            }

                            // Format and insert each row into the document.
                            block.forEach { row ->
                                row.forEachIndexed { i, word ->
                                    println(word.padEnd(maxLengths[i]))
                                    doc.insertString(doc.length, word.padEnd(maxLengths[i]), style)
                                }
                                doc.insertString(doc.length, "\n", style)
                            }

                            // Ensure the latest content is visible.
                            scrollToBottom()
                        }
                    }
                }
            }

            // climbing to next floor
            "climb" -> {
                // checking if in ladder room
                if (model.hero.room != model.map.endRoom) {
                    respond("there's no ladder to the next floor in this room!")
                    return
                }

                respond("you climb up to the next floor")
                // incrementing floor value so everything can be scaled accordingly
                model.floor += 1
                // generating new map for floor
                model.map = Map(model.mapSize, model.floor)
                model.hero.room = model.map.startRoom
                // clearing map and updating info
                clearMap()
                updateMap()
                updateInfo()
            }

            // info about available commands
            "help" -> {
                if (splitInput.size < 2) {
                    respond("prints information about available commands")
                    respond("use with subcommands to get information about them")
                    respond("usage: help [command]")
                    respond("commands:")
                    respond("- move")
                    respond("- pickup")
                    respond("- drop")
                    respond("- equip")
                    respond("- unequip")
                    respond("- use")
                    respond("- inspect")
                    respond("shop specific")
                    respond("- sell")
                    respond("- buy")
                    respond("- info")
                    respond("end room specific")
                    respond("- climb")
                    return
                }

                when (splitInput[1]) {
                    "move" -> {
                        respond("used to move between rooms")
                        respond("usage: move [direction]")
                        respond("directions:")
                        respond("- north")
                        respond("- east")
                        respond("- south")
                        respond("- west")
                    }

                    "pickup" -> {
                        respond("pickup an item from the room to your inventory")
                        respond("- usage: pickup [item class] [item id]")
                    }

                    "drop" -> {
                        respond("drop an item from your inventory to the room")
                        respond("usage: drop [item class] [item id]")
                    }

                    "equip" -> {
                        respond("equip an item from your inventory")
                        respond("usage: equip [item class] [item id]")
                    }

                    "unequip" -> {
                        respond("unequip your equipped armor/weapon")
                        respond("usage: unequip [item class]")
                    }

                    "use" -> {
                        respond("use a consumable in your inventory")
                        respond("usage: use [consumable id]")
                        respond("warning: using a throwing knife on yourself *will* hurt you")
                    }

                    "inspect" -> {
                        respond("inspect the room you're in")
                        respond("usage: inspect")
                    }

                    "sell" -> {
                        respond("only usable in shops")
                        respond("sell an item from your inventory for gold")
                        respond("usage: shop sell [item class] [item id]")
                    }

                    "buy" -> {
                        respond("only usable in shops")
                        respond("trade gold against an item in a shop")
                        respond("usage: shop buy [item class] [item id]")
                    }

                    "info" -> {
                        respond("only usable in shops")
                        respond("get info about shop items")
                        respond("usage: shop info")
                    }

                    "climb" -> {
                        respond("only usable in end rooms")
                        respond("used to advance to the next floor of the tower")
                    }

                    else -> {
                        respond("unrecognized command ${splitInput[1]}")
                    }
                }
            }

            else -> {
                respond("command ${splitInput[0]} doesn't exist!")
                return
            }
        }
    }

    // creating selection of weapons of a user-specified type from an inventory (such as room or player)
    // useful helper function
    private fun createSelection(type: String, inventory: Inventory): List<Item> {
        val selection: List<Item>

        when (type) {
            "weapon" -> {
                selection = inventory.filterIsInstance<Weapon>()
            }

            "armor" -> {
                selection = inventory.filterIsInstance<Armor>()
            }

            "consumable" -> {
                selection = inventory.filterIsInstance<Consumable>()
            }

            else -> {
                selection = listOf()
            }
        }

        return selection
    }

    // resetting everything back to the beginning when hero dies
    private fun heroDeath() {
        respond("you died! the next hero approaches the tower.")
        model.floor = 0
        model.map = Map(model.mapSize, model.floor)
        model.hero = Hero("", model.map.startRoom)
        clearMap()
        updateInfo()
        updateMap()
    }
}
