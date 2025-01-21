package view.content

import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel

class Sidebar(mapModel: DefaultTableModel, infoModel: DefaultTableModel) : JPanel() {
    val map = JTable(mapModel)
    val information = JTable(infoModel)
    val informationColumnWidths = arrayOf(0.25, 0.45, 0.15, 0.15)

    init {
        layout = BorderLayout(0, 10)
        border = BorderFactory.createEmptyBorder()

        val cellSize = 30
        for (i in 0..<map.columnCount) {
            map.columnModel.getColumn(i).preferredWidth = cellSize
            map.columnModel.getColumn(i).cellRenderer = CenteredTableRenderer()
        }

        map.setDefaultRenderer(String::class.java, CenteredTableRenderer())
        map.rowHeight = cellSize
        map.isEnabled = false
        map.setShowGrid(false)

        preferredSize = Dimension(cellSize * map.columnCount, map.preferredSize.height)

        add(map, BorderLayout.NORTH)

        information.autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS
        information.isEnabled = false
        information.setShowGrid(false)

        add(information, BorderLayout.SOUTH)
    }

    class CenteredTableRenderer : DefaultTableCellRenderer() {
        init {
            horizontalTextPosition = CENTER
        }
    }
}