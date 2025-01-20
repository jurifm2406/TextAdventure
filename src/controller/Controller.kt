package controller

import model.Model
import view.MainFrame
import kotlin.system.exitProcess

class Controller {
    private val model = Model("test")
    private val view = MainFrame(model)

    init {
        view.menuBar.exit.addActionListener { exitProcess(0) }
        view.content.input.addActionListener {
            parseInput(view.content.input.text)
            model.outputModel.insertString(
                model.outputModel.length,
                view.content.input.text + "\n",
                null
            )
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
        println(input)
    }
}

