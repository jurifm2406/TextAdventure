package view.content

import java.awt.BorderLayout
import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.JTable

class Sidebar : JPanel() {
    val map = JTable(9, 9)

    init {
        layout = BorderLayout(0, 10)
        border = BorderFactory.createEmptyBorder()

        for (i in 0..<map.columnCount) {
            map.columnModel.getColumn(i).minWidth = 25
            map.columnModel.getColumn(i).maxWidth = 25
        }
        map.rowHeight = 25
        map.isEnabled = false
        add(map, BorderLayout.NORTH)
    }
}