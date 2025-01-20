package controller

import model.Model
import model.objects.world.RoomNotThereException
import model.print
import view.MainFrame
import kotlin.system.exitProcess

class Controller {
    private val model = Model("test")
    private val view = MainFrame(model)

    init {
        view.menuBar.exit.addActionListener { exitProcess(0) }
        view.content.input.addActionListener {
            model.outputModel.print(view.content.input.text)
            parseInput(view.content.input.text.lowercase())
            view.content.input.text = ""
        }
        updateMap()
    }

    fun updateMap() {
        val displayMap = model.map.export()
        displayMap[model.hero.room.coords.x][model.hero.room.coords.y] = "o"
        model.mapModel.setDataVector(displayMap, arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8"))
    }

    fun parseInput(input: String) {
        val splitInput = input.split(" ")

        if (splitInput[0] in Commands.root) {
            if (splitInput[0] == "move") {
                if (splitInput[1] in Commands.movement) {
                    try {
                        model.map.move(Commands.movement.indexOf(splitInput[1]), model.hero)
                        updateMap()
                        model.outputModel.print("Moved to the ${splitInput[1]}")
                    } catch (e: RoomNotThereException) {
                        model.outputModel.print("There's no room in that direction!")
                    }
                } else {
                    model.outputModel.print("This direction doesn't exist!")
                }
            }
        } else {
            model.outputModel.print("This command doesn't exist!")
        }
    }
}

