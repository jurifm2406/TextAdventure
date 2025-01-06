package controller

import view.MainFrame
import kotlin.system.exitProcess

class Controller {
    private val view = MainFrame()

    init {
        view.menuBar.exit.addActionListener { exitProcess(0) }
    }
}