package controller

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
            view.content.output.respond(view.content.input.text, false)
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
            view.content.output.respond("no command specified!")
            return
        }
        if (combat != null) {

            when (combat!!.combatParse(splitInput)) {
                0 -> return
                1 -> {
                    combat = null
                    return
                }

                2 -> {
                    heroDeath()
                    combat = null
                    return
                }

                3 -> {
                    for (room in model.map.neighbours(model.hero.room.coords)) {
                        if (room == null) {
                            continue
                        }
                        // could be random room or previous room
                        model.hero.room = room

                        updateMap()
                        combat = null

                        break
                    }
                    return
                }
            }
            return
        }

        splitInput.forEach { it.lowercase() }

        when (splitInput[0]) {
            "move" -> {
                if (splitInput.size < 2) {
                    view.content.output.respond("usage: move [direction]!")
                    return
                }

                if (splitInput[1] !in movement.keys) {
                    view.content.output.respond("this direction doesn't exist!")
                    return
                }

                try {
                    model.map.move(movement[splitInput[1]] as Int, model.hero)
                    updateMap()
                    view.content.output.respond("moved to the ${splitInput[1]}")

                    if (model.hero.room.entities.filterIsInstance<Enemy>().isNotEmpty()) {
                        for (i in 0..<model.hero.room.entities.size) {
                            view.content.output.respond(model.hero.room.entities[i].name)
                        }
                        combat = Combat(model.hero.room.entities.filterIsInstance<Enemy>()[0], model.hero, view)
                        view.content.output.respond("you entered combat")
                    }
                } catch (e: RoomNotThereException) {
                    view.content.output.respond(e.message)
                }
            }

            "inventory" -> {
                if (splitInput.size < 2) {
                    view.content.output.respond("usage: inventory [action]")
                    return
                }

                when (splitInput[1]) {
                    "drop" -> {
                        if (splitInput.size < 4) {
                            view.content.output.respond("usage: drop [item class] [item id]")
                            return
                        }

                        val selection = createSelection(splitInput[2], model.hero.inventory)

                        if (selection.isEmpty()) {
                            view.content.output.respond("there are no items of type ${splitInput[2]} in your inventory")
                        }

                        try {
                            model.hero.drop(selection[splitInput[3].toInt()])
                            updateInfo()
                            view.content.output.respond("dropped ${splitInput[2]} ${selection[splitInput[3].toInt()].name}")
                        } catch (e: IndexOutOfBoundsException) {
                            view.content.output.respond("item id ${splitInput[3]} doesn't correspond to an item in your inventory")
                        } catch (e: ItemNotThereException) {
                            view.content.output.respond(e.message)
                        }
                    }

                    "equip" -> {
                        if (splitInput.size < 4) {
                            view.content.output.respond("usage: equip [item class] [item id]")
                            return
                        }

                        if (splitInput[2] == "consumable") {
                            view.content.output.respond("can't equip a consumable")
                            return
                        }

                        val selection = createSelection(splitInput[2], model.hero.inventory)

                        if (selection.isEmpty()) {
                            view.content.output.respond("there are no items of type ${splitInput[2]} in your inventory")
                        }

                        try {
                            model.hero.equip(selection[splitInput[3].toInt()])
                            updateInfo()
                            view.content.output.respond("equipped ${splitInput[2]} ${selection[splitInput[3].toInt()].name}")
                        } catch (e: IndexOutOfBoundsException) {
                            view.content.output.respond("item id ${splitInput[3]} doesn't correspond to an item in your inventory")
                        } catch (e: ItemNotThereException) {
                            view.content.output.respond(e.message)
                        }
                    }

                    "unequip" -> {
                        if (splitInput.size < 3) {
                            view.content.output.respond("usage: unequip [item class]")
                            return
                        }

                        if (splitInput[2] == "consumable") {
                            view.content.output.respond("can't unequip a consumable")
                            return
                        }

                        when (splitInput[2]) {
                            "armor" -> {
                                view.content.output.respond("unequipped armor ${model.hero.armor}")
                                model.hero.unequip(splitInput[2])
                                updateInfo()
                            }

                            "weapon" -> {
                                view.content.output.respond("unequipped weapon ${model.hero.weapon}")
                                model.hero.unequip(splitInput[2])
                                updateInfo()
                            }

                            else -> {
                                view.content.output.respond("can't unequip item of type ${splitInput[2]}")
                            }
                        }
                    }

                    "use" -> {
                        if (splitInput.size < 3) {
                            view.content.output.respond("usage: use [consumable id]")
                        }

                        val selection = model.hero.inventory.filterIsInstance<Consumable>()

                        try {
                            model.hero.use(selection[splitInput[2].toInt()], model.hero)
                            updateInfo()
                            updateMap()
                        } catch (e: IndexOutOfBoundsException) {
                            view.content.output.respond("no consumable with that index!")
                        }
                    }

                    else -> {
                        view.content.output.respond("command ${splitInput[1]} doesn't exist!")
                        return
                    }
                }
            }

            "room" -> {
                if (splitInput.size < 2) {
                    view.content.output.respond("usage: room [action]")
                    return
                }

                when (splitInput[1]) {
                    "pickup" -> {
                        if (splitInput.size < 4) {
                            view.content.output.respond("usage: pickup [item class] [item id]")
                            return
                        }

                        val selection = createSelection(splitInput[2], model.hero.room.inventory)

                        if (selection.isEmpty()) {
                            view.content.output.respond("there are no items of type ${splitInput[2]} in the room")
                        }

                        try {
                            model.hero.pickup(selection[splitInput[3].toInt()])
                            updateInfo()
                            view.content.output.respond("picked up ${splitInput[2]} ${selection[splitInput[3].toInt()].name}")
                        } catch (e: IndexOutOfBoundsException) {
                            view.content.output.respond("this item id doesn't correspond to an item in the current room")
                        } catch (e: ItemNotThereException) {
                            view.content.output.respond(e.message)
                        }
                    }

                    "inspect" -> {
                        if (model.hero.room.inventory.export().isEmpty()) {
                            view.content.output.respond("there are no items in this room!")
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

                    else -> {
                        view.content.output.respond("command ${splitInput[1]} doesn't exist!")
                    }
                }
            }

            "help" -> {
                if (splitInput.size < 2) {
                    view.content.output.respond("prints information about available commands")
                    view.content.output.respond("usage: help [command]")
                    view.content.output.respond("commands:")
                    view.content.output.respond("- move")
                    view.content.output.respond("- inventory")
                    view.content.output.respond("- room")
                    return
                }

                when (splitInput[1]) {
                    "move" -> {
                        view.content.output.respond("used to move between rooms")
                        view.content.output.respond("usage: move [direction]")
                        view.content.output.respond("directions:")
                        view.content.output.respond("- north")
                        view.content.output.respond("- east")
                        view.content.output.respond("- south")
                        view.content.output.respond("- west")
                    }

                    "inventory" -> {
                        view.content.output.respond("used to interact with your inventory")
                        view.content.output.respond("usage: inventory [action]")
                        view.content.output.respond("actions:")
                        view.content.output.respond("- drop [item class] [item id]: drop item into room")
                        view.content.output.respond("- equip [item class] [item id]: equip item")
                        view.content.output.respond("- unequip [item class]: unequip equipped item")
                        view.content.output.respond("- use [consumable id]: use specified consumable")
                    }

                    "room" -> {
                        view.content.output.respond("used to interact with the current room")
                        view.content.output.respond("usage: room [action]")
                        view.content.output.respond("- pickup [item class] [item id]: pickup item from room")
                        view.content.output.respond("- inspect: list items in room")
                    }

                    else -> {
                        view.content.output.respond("unrecognized command ${splitInput[1]}")
                    }
                }
            }

            else -> {
                view.content.output.respond("command ${splitInput[0]} doesn't exist!")
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
        view.content.output.respond("You died!")
        for (item in model.hero.inventory) {
            model.hero.room.inventory.add(item)
        }
        model.hero.inventory.clear()
        view.content.output.respond("You respawned")
        view.content.sidebar.map.setValueAt("x", model.hero.room.coords.x, model.hero.room.coords.y)
        model.hero.room = model.map.startRoom
        model.hero.health = 300
        updateMap()
    }
}
