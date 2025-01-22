package view

import model.Model
import view.content.Content
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JFrame

class MainFrame(model: Model) : JFrame() {
    val menuBar = MenuBar()
    val content = Content(model.infoModel, model.mapSize)

    init {
        size = Dimension(1600, 900)
        jMenuBar = menuBar

        add(content, BorderLayout.CENTER)

        defaultCloseOperation = EXIT_ON_CLOSE
        isVisible = true
    }
}