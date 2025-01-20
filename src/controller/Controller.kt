package controller

import model.Model
import model.objects.base.item.Armor
import model.objects.base.item.Consumable
import model.objects.base.item.Weapon
import model.objects.world.RoomNotThereException
import view.MainFrame
import kotlin.math.round
import kotlin.system.exitProcess

class Controller {
    private val model = Model("test")
    private val view = MainFrame(model)

    init {
        view.menuBar.exit.addActionListener { exitProcess(0) }
        view.content.input.addActionListener {
            view.content.output.append("${view.content.input.text}\n")
            parseInput(view.content.input.text.lowercase())
            view.content.input.text = ""
        }
        updateMap()
        updateInfo()
    }

    fun updateMap() {
        val displayMap = model.map.export()
        displayMap[model.hero.room.coords.x][model.hero.room.coords.y] = "o"
        model.mapModel.setDataVector(displayMap, arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8"))
    }

    fun updateInfo() {
        val infoData: MutableList<Array<String>> = mutableListOf()

        infoData.add(arrayOf("floor", "", model.floor.toString()))
        infoData.add(arrayOf("health", "", model.hero.health.toString()))
        infoData.add(Array(3) { "" })
        infoData.add(arrayOf("WEAPONS", "", ""))
        infoData.add(arrayOf("name", "", "dmg"))
        infoData.add(Array(3) { "" })

        for (weapon in model.hero.inventory.content.filterIsInstance<Weapon>()) {
            infoData.add(arrayOf(weapon.name, "", weapon.damage.toString()))
        }

        infoData.add(arrayOf("ARMOR", "", ""))
        infoData.add(arrayOf("name", "abs", "neg"))
        infoData.add(Array(3) { "" })

        for (armor in model.hero.inventory.content.filterIsInstance<Armor>()) {
            infoData.add(arrayOf(armor.name, armor.absorbtion.toString(), round(armor.negation).toString()))
        }

        infoData.add(arrayOf("CONSUMABLE", "", ""))
        infoData.add(arrayOf("name", "", "effect"))
        infoData.add(Array(3) { "" })

        for (consumable in model.hero.inventory.content.filterIsInstance<Consumable>()) {
            infoData.add(arrayOf(consumable.name, "", consumable.description))
        }

        model.infoModel.setDataVector(infoData.toTypedArray(), arrayOf("0", "1", "2"))

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
                        view.content.output.append("Moved to the ${splitInput[1]}")
                    } catch (e: RoomNotThereException) {
                        view.content.output.append("There's no room in that direction!")
                    }
                } else {
                    view.content.output.append("This direction doesn't exist!")
                }
            }
        } else {
            view.content.output.append("This command doesn't exist!")
        }
    }
}
