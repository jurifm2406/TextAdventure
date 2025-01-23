package controller

import model.Model
import model.objects.base.item.Armor
import model.objects.base.item.Consumable
import model.objects.base.item.Weapon
import model.objects.base.entities.Enemy
import model.objects.base.entities.Entity
import model.objects.base.entities.Hero
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

    val commands = mapOf(
        "move" to mapOf(
            "north" to Directions.WEST,
            "east" to Directions.SOUTH,
            "south" to Directions.EAST,
            "west" to Directions.NORTH
        ),
        "inventory" to mapOf(
            "drop" to null,
            "take" to null,
            "info" to null
        ),
        "help" to mapOf(
            null to "information about the available commands",
            "move" to "movement between rooms, available options: north, east, south, west",
            "inventory" to mapOf(
                null to "used to interact with your inventory. usage: inventory [subcommand] [item id]",
                "drop" to "drop an item from your inventory into the current room",
                "take" to "take an inventory from the current room into your inventory",
                "info" to "get long-form information about an item"
            )
        )
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

        model.hero.inventory.content.filterIsInstance<Weapon>().forEachIndexed { id, weapon ->
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

        model.hero.inventory.content.filterIsInstance<Armor>().forEachIndexed { id, armor ->
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

        model.hero.inventory.content.filterIsInstance<Consumable>().forEachIndexed { id, consumable ->
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
        val splitInput = input.split(" ")

        if (splitInput[0] in Commands.root) {
            if (splitInput[0] == "move") {
                if (splitInput[1] in Commands.movement) {
                    try {
                        model.map.move(Commands.movement.indexOf(splitInput[1]), model.hero)
                        updateMap()
                        view.content.output.respond("moved to the ${splitInput[1]}.")

                        if(model.map.map[model.hero.room.coords.x][model.hero.room.coords.y]!!.entities.filterIsInstance<Enemy>().isNotEmpty()){
                            for (i in 0 ..< model.map.map[model.hero.room.coords.x][model.hero.room.coords.y]!!.entities.size){
                                view.content.output.respond(model.map.map[model.hero.room.coords.x][model.hero.room.coords.y]!!.entities[i].name)
                            }
                            combat(model.hero, model.map.map[model.hero.room.coords.x][model.hero.room.coords.y]!!.entities.filterIsInstance<Enemy>()[0])
                        }
                        for (i in 0 ..< model.map.map[model.hero.room.coords.x][model.hero.room.coords.y]!!.inventory.content.size){
                            view.content.output.respond(model.map.map[model.hero.room.coords.x][model.hero.room.coords.y]!!.inventory.content[i].name)
                        }
                    } catch (e: RoomNotThereException) {
                        view.content.output.respond(e.message)
                    }
                } else {
                    view.content.output.respond("this direction doesn't exist")
                }
            }
        } else {
            view.content.output.respond("this command doesn't exist")
        }


    }
    fun combat(hero: Hero, defender: Entity){
        return
    }

}
