package controller

import model.Model
import view.MainFrame
import kotlin.system.exitProcess

class Controller {
    private val model = Model()
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
        model.mapModel.setDataVector(model.map.export(), arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8"))
    }
}

fun parseInput(input: String) {
    println(input)
}