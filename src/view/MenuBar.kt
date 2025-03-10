package view

import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem

/**
 * the menu bar, used to exit the program, initially intended to have more options
 */
class MenuBar : JMenuBar() {
    val file: JMenu = JMenu("File")
    val exit: JMenuItem = JMenuItem("Exit")

    init {
        file.add(exit)
        add(file)
    }
}