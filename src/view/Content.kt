package view

import view.content.Sidebar
import java.awt.BorderLayout
import java.awt.Point
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.table.DefaultTableModel

/**
 * view class containing player input and game output
 *
 * @param infoModel used to initialize info table in sidebar
 * @param mapSize used to initialize map in sidebar
 */
class Content(infoModel: DefaultTableModel, mapSize: Point) : JPanel() {
    val sidebar = Sidebar(infoModel, mapSize)
    val output = JTextPane()
    val input = JTextField()
    val scroll = JScrollPane(output)

    init {
        border = EmptyBorder(10, 10, 10, 10)
        layout = BorderLayout(10, 10)

        add(sidebar, BorderLayout.EAST)

        // make output non-editable and configure scrollbar
        output.isEditable = false
        scroll.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED

        add(scroll, BorderLayout.CENTER)

        add(input, BorderLayout.SOUTH)
    }
}