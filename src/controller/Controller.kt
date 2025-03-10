package controller

import model.Data
import model.Map
import model.Model
import model.objects.base.Inventory
import model.objects.base.entities.Enemy
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
    private val model = Model("test")
    private val view = View(model)
    private var combat: Combat? = null
    private val history: MutableList<String> = mutableListOf()
    private var historyIndex = -1

    private val movement = mapOf(
        "north" to Directions.WEST,
        "east" to Directions.SOUTH,
        "south" to Directions.EAST,
        "west" to Directions.NORTH
    )

    init {
        view.menuBar.exit.addActionListener { exitProcess(0) }
        view.content.input.addActionListener {
            respond(view.content.input.text, false)
            history.add(0, view.content.input.text)
            parseInput(view.content.input.text)
            view.content.input.text = ""
        }
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
        updateMap()
        updateInfo()
    }

    private fun respond(message: String?, bold: Boolean = true) {
        view.content.output.respond(message, bold)
        scrollToBottom()
    }

    private fun scrollToBottom() {
        view.content.scroll.verticalScrollBar.value = view.content.scroll.verticalScrollBar.maximum
    }

    private fun updateMap() {
        view.content.sidebar.map.setValueAt("o", model.hero.room.coords.x, model.hero.room.coords.y)

        for (room in model.map.neighbours(model.hero.room.coords)) {
            if (room == null) {
                continue
            }
            view.content.sidebar.map.setValueAt("x", room.coords.x, room.coords.y)
        }
    }

    private fun clearMap() {
        for (i in 0..<model.mapSize.x) {
            for (j in 0..<model.mapSize.y) {
                view.content.sidebar.map.setValueAt(" ", i, j)
            }
        }
        updateMap()
    }

    private fun updateInfo() {
        val infoData: MutableList<Array<String>> = mutableListOf()

        infoData.add(arrayOf("floor", "", model.floor.toString()))
        infoData.add(arrayOf("health", "", model.hero.health.toString()))
        infoData.add(Array(3) { "" })

        infoData.add(arrayOf("WEAPONS", "", ""))
        infoData.add(arrayOf("id", "name", "dmg"))

        infoData.add(arrayOf("x", model.hero.weapon.name, model.hero.weapon.damage.toString()))

        model.hero.inventory.filterIsInstance<Weapon>().forEachIndexed { id, weapon ->
            infoData.add(arrayOf(id.toString(), weapon.name, weapon.damage.toString()))
        }

        infoData.add(Array(3) { "" })

        infoData.add(arrayOf("ARMOR", "", ""))
        infoData.add(arrayOf("id", "name", "abs"))

        infoData.add(
            arrayOf(
                "x",
                model.hero.armor.name,
                model.hero.armor.absorption.toString(),
            )
        )

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

        infoData.add(arrayOf("ITEMS", "", ""))
        infoData.add(arrayOf("id", "name", "effect"))

        model.hero.inventory.filterIsInstance<Consumable>().forEachIndexed { id, consumable ->
            infoData.add(arrayOf(id.toString(), consumable.name, consumable.description))
        }

        infoData.add(Array(3) { "" })

        model.infoModel.setDataVector(infoData.toTypedArray(), arrayOf("0", "1", "2"))

        view.content.sidebar.informationColumnWidths.forEachIndexed { column, widthPercentage ->
            view.content.sidebar.information.columnModel.getColumn(column).preferredWidth =
                (view.content.sidebar.information.width * widthPercentage).toInt()
        }
    }

    private fun parseInput(input: String) {
        val splitInput = input.split(" ")

        if (splitInput.isEmpty()) {
            respond("no command specified!")
            return
        }
        if (combat != null) {
            when (combat!!.combatParse(splitInput)) {
                0 -> {
                    updateInfo()
                    updateMap()
                    return
                }

                1 -> {
                    combat = null
                    updateInfo()
                    updateMap()
                    return
                }

                2 -> {
                    heroDeath()
                    updateInfo()
                    updateMap()

                    combat = null
                    return
                }

                3 -> {
                    // move to last room
                    model.hero.room = model.hero.lastRoom

                    updateInfo()
                    updateMap()
                    combat = null
                    return
                }
            }
            return
        }

        splitInput.forEach { it.lowercase() }

        when (splitInput[0]) {
            "move" -> {
                if (splitInput.size < 2) {
                    respond("usage: move [direction]")
                    return
                }

                if (splitInput[1] !in movement.keys) {
                    respond("this direction doesn't exist!")
                    return
                }

                try {
                    model.map.move(movement[splitInput[1]] as Int, model.hero)
                    updateMap()
                    respond("moved to the ${splitInput[1]}")

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
                } catch (e: RoomNotThereException) {
                    respond(e.message)
                }
            }

            "drop" -> {
                if (splitInput.size < 3) {
                    respond("usage: drop [item class] [item id]")
                    return
                }

                val selection = createSelection(splitInput[1], model.hero.inventory)

                if (selection.isEmpty()) {
                    respond("there are no items of type ${splitInput[1]} in your inventory")
                }

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

            "equip" -> {
                if (splitInput.size < 3) {
                    respond("usage: equip [item class] [item id]")
                    return
                }

                if (splitInput[1] == "consumable") {
                    respond("can't equip a consumable")
                    return
                }

                val selection = createSelection(splitInput[1], model.hero.inventory)

                if (selection.isEmpty()) {
                    respond("there are no items of type ${splitInput[1]} in your inventory")
                }

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

            "unequip" -> {
                if (splitInput.size < 2) {
                    respond("usage: unequip [item class]")
                    return
                }

                if (splitInput[1] == "consumable") {
                    respond("can't unequip a consumable")
                    return
                }

                when (splitInput[1]) {
                    "armor" -> {
                        respond("unequipped armor ${model.hero.armor}")
                        model.hero.unequip(splitInput[1])
                        updateInfo()
                    }

                    "weapon" -> {
                        respond("unequipped weapon ${model.hero.weapon}")
                        model.hero.unequip(splitInput[1])
                        updateInfo()
                    }

                    else -> {
                        respond("can't unequip item of type ${splitInput[1]}")
                    }
                }
            }

            "use" -> {
                if (splitInput.size < 2) {
                    respond("usage: use [consumable id]")
                }

                val selection = model.hero.inventory.filterIsInstance<Consumable>()

                try {
                    model.hero.use(selection[splitInput[1].toInt()], model.hero)
                    updateInfo()
                    updateMap()
                } catch (e: IndexOutOfBoundsException) {
                    respond("no consumable with that index!")
                }
            }

            "pickup" -> {
                if (splitInput.size < 3) {
                    respond("usage: pickup [item class] [item id]")
                    return
                }

                val selection = createSelection(splitInput[1], model.hero.room.inventory)

                if (selection.isEmpty()) {
                    respond("there are no items of type ${splitInput[1]} in the room")
                }

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

            "inspect" -> {
                if (model.hero.room.inventory.export().isEmpty()) {
                    respond("there are no items in this room!")
                    return
                }

                val style = SimpleAttributeSet()
                StyleConstants.setFontFamily(style, Font.MONOSPACED)

                val doc = view.content.output.styledDocument

                model.hero.room.inventory.export().forEach { block ->
                    val maxLengths = Array(4) { 0 }
                    block.forEach { row ->
                        row.forEachIndexed { i, word ->
                            if (word.length + 2 > maxLengths[i]) {
                                maxLengths[i] = word.length + 2
                            }
                        }
                    }

                    block.forEach { row ->
                        row.forEachIndexed { i, word ->
                            println(word.padEnd(maxLengths[i]))
                            doc.insertString(doc.length, word.padEnd(maxLengths[i]), style)
                        }

                        doc.insertString(doc.length, "\n", style)
                    }
                }
            }

            "shop" -> {
                if (model.hero.room != model.map.shopRoom) {
                    respond("You are not in a shop room")
                    return
                }
                when (splitInput[1]) {
                    "sell" -> {
                        if (splitInput.size < 4) {
                            // add usage
                            return
                        }
                        val selection = createSelection(splitInput[2], model.hero.room.inventory)

                        if (selection.isEmpty()) {
                            respond("there are no items of type ${splitInput[2]} in your inventory")
                        }

                        try {
                            model.hero.inventory.remove(selection[splitInput[3].toInt()])
                            model.hero.coins += 15
                        } catch (e: IndexOutOfBoundsException) {
                            respond("this item id doesn't correspond to an item in your inventory")
                        } catch (e: ItemNotThereException) {
                            respond(e.message)
                        }
                    }

                    "buy" -> {
                        if (splitInput.size < 4) {
                            // add usage
                            return
                        }
                        val inv = Inventory(Data.weapons.size + Data.armors.size + Data.consumables.size)
                        for (weapon in Data.weapons) {
                            val tWeapon = weapon.copy()
                            tWeapon.damage = ((weapon.damage + 6) * model.map.scale).toInt()
                            inv.add(tWeapon)
                        }
                        for (armor in Data.armors) {
                            val tArmor = armor.copy()
                            tArmor.absorption = ((armor.absorption + 3) * model.map.scale).toInt()
                            inv.add(tArmor)
                        }
                        for (consumable in Data.consumables) {
                            inv.add(consumable.copy())
                        }
                        val selection = createSelection(splitInput[2], inv)
                        try {
                            if (model.hero.coins >= 150) {
                                model.hero.inventory.add(selection[splitInput[3].toInt()])
                                model.hero.coins -= 150
                                respond("You bought a ${selection[splitInput[3].toInt()].name}")
                            } else {
                                respond("You don't have enough coins left!")
                            }
                        } catch (e: IndexOutOfBoundsException) {
                            respond("this item id doesn't correspond to an item in the shop")
                        } catch (e: ItemNotThereException) {
                            respond(e.message)
                        }
                    }

                    "info" -> {
                        val tInv = Inventory(Data.weapons.size + Data.armors.size + Data.consumables.size)
                        for (weapon in Data.weapons) {
                            tInv.add(weapon)
                        }
                        for (armor in Data.armors) {
                            tInv.add(armor)
                        }
                        for (consumable in Data.consumables) {
                            tInv.add(consumable)
                        }

                        val style = SimpleAttributeSet()
                        StyleConstants.setFontFamily(style, Font.MONOSPACED)

                        val doc = view.content.output.styledDocument

                        tInv.export().forEach { block ->
                            val maxLengths = Array(4) { 0 }
                            block.forEach { row ->
                                row.forEachIndexed { i, word ->
                                    if (word.length + 2 > maxLengths[i]) {
                                        maxLengths[i] = word.length + 2
                                    }
                                }
                            }

                            block.forEach { row ->
                                row.forEachIndexed { i, word ->
                                    println(word.padEnd(maxLengths[i]))
                                    doc.insertString(doc.length, word.padEnd(maxLengths[i]), style)
                                }

                                doc.insertString(doc.length, "\n", style)
                            }
                        }
                    }
                }
            }

            "climb" -> {
                if (model.hero.room == model.map.endRoom) {
                    respond("you climb up to the next floor")
                    model.floor += 1
                    model.map = Map(model.mapSize, model.floor)
                    model.hero.room = model.map.startRoom
                    clearMap()
                    updateMap()
                    updateInfo()
                } else {
                    respond("there's no ladder to the next floor in this room!")
                }
            }

            "help" -> {
                if (splitInput.size < 2) {
                    respond("prints information about available commands")
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

    private fun heroDeath() {
        respond("You died!")
        for (item in model.hero.inventory) {
            model.hero.room.inventory.add(item)
        }
        model.hero.inventory.clear()
        respond("You respawned")
        view.content.sidebar.map.setValueAt("x", model.hero.room.coords.x, model.hero.room.coords.y)
        model.hero.room = model.map.startRoom
        model.hero.health = 300
    }
}
