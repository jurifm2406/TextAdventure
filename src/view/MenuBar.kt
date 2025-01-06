package view

import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem

class MenuBar : JMenuBar() {
    val file: JMenu = JMenu("File")
    val exit: JMenuItem = JMenuItem("Exit")

    init {
        file.add(exit)
        add(file)
    }
}