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
    }
}

fun parseInput(input: String) {
    println(input)
}