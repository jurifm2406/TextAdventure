package model

import javax.swing.table.DefaultTableModel

class MapModel(size: Int) : DefaultTableModel(Array(size) { Array(size) { "" } }, arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8)) {
    override fun getColumnClass(columnIndex: Int): Class<*> {
        return getValueAt(0, columnIndex).javaClass
    }
}