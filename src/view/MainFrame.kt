package view

import view.content.Content
import java.awt.BorderLayout
import javax.swing.JFrame

class MainFrame : JFrame() {
    val menuBar = MenuBar()
    val content = Content()

    init {
        add(menuBar, BorderLayout.NORTH)

        add(content, BorderLayout.CENTER)

        extendedState = MAXIMIZED_BOTH
        defaultCloseOperation = EXIT_ON_CLOSE
        isVisible = true
    }
}