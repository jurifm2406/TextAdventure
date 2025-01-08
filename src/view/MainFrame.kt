package view

import view.content.Content
import javax.swing.*

class MainFrame : JFrame() {
    val menuBar = MenuBar()
    val content = Content()

    init {
        jMenuBar = menuBar

        add(content)

        extendedState = MAXIMIZED_BOTH
        defaultCloseOperation = EXIT_ON_CLOSE
        isVisible = true
    }
}