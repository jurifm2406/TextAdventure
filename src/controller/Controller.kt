package controller

import model.Model
import model.objects.base.entities.Enemy
import model.objects.base.item.Armor
import model.objects.base.item.Consumable
import model.objects.base.item.Item
import model.objects.base.item.Weapon
import model.objects.world.Directions
import model.objects.world.ItemNotThereException
import model.objects.world.RoomNotThereException
import view.View
import javax.swing.JTextPane
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants
import kotlin.math.round
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
    private var inCombat = false
    private var combat : Combat? = null

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
            parseInput(view.content.input.text.lowercase())
            view.content.input.text = ""
        }
        updateMap()
        updateInfo()
    }

    fun updateMap() {
        view.content.sidebar.map.setValueAt("o", model.hero.room.coords.x, model.hero.room.coords.y)

        for (room in model.map.neighbours(model.hero.room.coords)) {
            if (room == null) {
                continue
            }
            view.content.sidebar.map.setValueAt("x", room.coords.x, room.coords.y)
        }
    }

    fun updateInfo() {
        val infoData: MutableList<Array<String>> = mutableListOf()

        infoData.add(arrayOf("floor", "", "", model.floor.toString()))
        infoData.add(arrayOf("health", "", "", model.hero.health.toString()))
        infoData.add(Array(4) { "" })

        infoData.add(arrayOf("WEAPONS", "", "", ""))
        infoData.add(arrayOf("id", "name", "", "dmg"))

        infoData.add(arrayOf("x", model.hero.weapon.name, "", model.hero.weapon.damage.toString()))

        model.hero.inventory.filterIsInstance<Weapon>().forEachIndexed { id, weapon ->
            infoData.add(arrayOf(id.toString(), weapon.name, "", weapon.damage.toString()))
        }

        infoData.add(Array(4) { "" })

        infoData.add(arrayOf("ARMOR", "", "", ""))
        infoData.add(arrayOf("id", "name", "abs", "neg"))

        infoData.add(
            arrayOf(
                "x",
                model.hero.armor.name,
                model.hero.armor.absorption.toString(),
                model.hero.armor.negation.toString()
            )
        )

        model.hero.inventory.filterIsInstance<Armor>().forEachIndexed { id, armor ->
            infoData.add(
                arrayOf(
                    id.toString(),
                    armor.name,
                    armor.absorption.toString(),
                    round(armor.negation).toString()
                )
            )
        }

        infoData.add(Array(4) { "" })

        infoData.add(arrayOf("ITEMS", "", "", ""))
        infoData.add(arrayOf("id", "name", "", "effect"))

        model.hero.inventory.filterIsInstance<Consumable>().forEachIndexed { id, consumable ->
            infoData.add(arrayOf(id.toString(), consumable.name, "", consumable.description))
        }

        infoData.add(Array(4) { "" })

        model.infoModel.setDataVector(infoData.toTypedArray(), arrayOf("0", "1", "2", "3"))

        view.content.sidebar.informationColumnWidths.forEachIndexed { column, widthPercentage ->
            view.content.sidebar.information.columnModel.getColumn(column).preferredWidth =
                (view.content.sidebar.information.width * widthPercentage).toInt()
        }
    }

    fun parseInput(input: String) {
        val splitInput = input.lowercase().split(" ")

        if (splitInput.isEmpty()) {
            view.content.output.respond("no command specified!")
            return
        }
        if (inCombat) {
            combat!!.Combatparse(splitInput)

        } else {
            when (splitInput[0]) {
                "move" -> {
                    if (splitInput.size < 2) {
                        view.content.output.respond("need a direction!")
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
                            inCombat = true
                            combat = Combat(model.hero.room.entities.filterIsInstance<Enemy>()[0], model.hero, view)
                            view.content.output.respond("You entered Combat")
                        }

                    } catch (e: RoomNotThereException) {
                        view.content.output.respond(e.message)
                    }
                }

                "room" -> {
                    if (splitInput.size < 2) {
                        view.content.output.respond("need subcommand")
                        return
                    }

                    when (splitInput[1]) {
                        "pickup" -> {
                            if (splitInput.size < 4) {
                                view.content.output.respond("need item class!")
                                return
                            }

                            val selection: List<Item>

                            when (splitInput[2]) {
                                "weapon" -> {
                                    selection = model.hero.room.inventory.filterIsInstance<Weapon>()
                                }

                                "armor" -> {
                                    selection = model.hero.room.inventory.filterIsInstance<Armor>()
                                }

                                "consumable" -> {
                                    selection = model.hero.room.inventory.filterIsInstance<Consumable>()
                                }

                                else -> {
                                    view.content.output.respond("${splitInput[2]} is no valid item class!")
                                    return
                                }
                            }

                            try {
                                model.hero.pickup(selection[splitInput[3].toInt()])
                            } catch (e: IndexOutOfBoundsException) {
                                view.content.output.respond("this item id doesn't correspond to an item in the current room")
                            } catch (e: ItemNotThereException) {
                                view.content.output.respond(e.message)
                            }
                        }

                        else -> {
                            view.content.output.respond("command ${splitInput[1]} doesn't exist!")
                        }
                    }
                }

                "inventory" -> {
                    if (splitInput.size < 4) {
                        view.content.output.respond("need class and id!")
                        return
                    }

                    when (splitInput[1]) {
                        "drop" -> {
                            val selection: List<Item>

                            when (splitInput[2]) {
                                "weapon" -> {
                                    selection = model.hero.inventory.filterIsInstance<Weapon>()
                                }

                                "armor" -> {
                                    selection = model.hero.inventory.filterIsInstance<Armor>()
                                }

                                "consumable" -> {
                                    selection = model.hero.inventory.filterIsInstance<Consumable>()
                                }

                                else -> {
                                    view.content.output.respond("${splitInput[2]} is no valid item class!")
                                    return
                                }
                            }

                            try {
                                model.hero.drop(selection[splitInput[3].toInt()])
                            } catch (e: java.lang.IndexOutOfBoundsException) {
                                view.content.output.respond("item id ${splitInput[2]} doesn't correspond to an item in your inventory")
                            } catch (e: ItemNotThereException) {
                                view.content.output.respond(e.message)
                            }
                        }

                        else -> {
                            view.content.output.respond("command ${splitInput[1]} doesn't exist!")
                            return
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
                            view.content.output.respond("- drop [item id]: drop item into room")
                            view.content.output.respond("- pickup [item id]: pickup item from room")
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
    }

}
