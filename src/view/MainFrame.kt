package view

import model.Model
import view.content.Content
import javax.swing.JFrame

class MainFrame(model: Model) : JFrame() {
    val menuBar = MenuBar()
    val content: Content

    init {
        jMenuBar = menuBar
        content = Content(model.outputModel)
        add(content)
        extendedState = MAXIMIZED_BOTH
        defaultCloseOperation = EXIT_ON_CLOSE
        isVisible = true
    }
}