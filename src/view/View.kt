package view

import model.Model
import view.content.Content
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JFrame

/**
 * view class holding everything that is actually seen on screen
 *
 * @param model the model, needed to bind some view components to model data
 */
class View(model: Model) : JFrame() {
    // initialization of menu bar
    val menuBar = MenuBar()

    // initialization of actual content, containing input, output and sidebar
    val content = Content(model.infoModel, model.mapSize)

    init {
        // initial size of game window
        size = Dimension(1600, 900)
        // attaching of menu bar to window
        jMenuBar = menuBar

        // adding of content class
        add(content, BorderLayout.CENTER)

        // exit process upon windows close
        defaultCloseOperation = EXIT_ON_CLOSE
        isVisible = true
    }
}