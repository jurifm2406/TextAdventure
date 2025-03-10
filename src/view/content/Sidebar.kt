package view.content

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Point
import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel

/**
 * the sidebar containing map and information about character
 *
 * @param infoModel model to use in the table containing info about game
 * @param mapSize size of map used to initialize the map display
 */
class Sidebar(infoModel: DefaultTableModel, mapSize: Point) : JPanel() {
    // jtable to display map
    val map = JTable(mapSize.x, mapSize.y)

    // jtable to display game info
    val information = JTable(infoModel)

    // width of information table columns
    val informationColumnWidths = arrayOf(0.25, 0.6, 0.15)

    init {
        layout = BorderLayout(0, 10)
        border = BorderFactory.createEmptyBorder()

        // set size of map display columns
        val cellSize = 30
        for (i in 0..<map.columnCount) {
            map.columnModel.getColumn(i).preferredWidth = cellSize
            map.columnModel.getColumn(i).cellRenderer = CenteredTableRenderer()
        }

        map.setDefaultRenderer(String::class.java, CenteredTableRenderer())
        map.rowHeight = cellSize
        // disabling editing of map
        map.isEnabled = false
        // disabling showing column and row separators
        map.setShowGrid(false)

        // set size of map display in general
        preferredSize = Dimension(cellSize * map.columnCount, map.preferredSize.height)

        // add map to sidebar
        add(map, BorderLayout.NORTH)

        // add info table to sidebar
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