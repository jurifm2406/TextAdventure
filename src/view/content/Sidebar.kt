package view.content

import model.MapModel
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer

class Sidebar(mapModel: MapModel) : JPanel() {
    val map: JTable = JTable(mapModel)

    init {

        layout = BorderLayout(0, 10)
        border = BorderFactory.createEmptyBorder()

        val cellSize = 20
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
    }

    class CenteredTableRenderer : DefaultTableCellRenderer() {
        init {
            horizontalTextPosition = CENTER
        }
    }
}