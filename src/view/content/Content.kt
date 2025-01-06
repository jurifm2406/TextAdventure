package view.content

import java.awt.BorderLayout
import javax.swing.*
import javax.swing.border.EmptyBorder

class Content : JPanel() {
    val sidebar = Sidebar()
    val output: JScrollPane
    val input = JTextField()

    init {
        border = EmptyBorder(10, 10, 10, 10)
        layout = BorderLayout(10, 10)

        add(sidebar, BorderLayout.EAST)

        val textArea = JTextArea()
        textArea.isEditable = false
        val scroll = JScrollPane(textArea)
        scroll.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
        output = scroll
        add(output)

        add(input, BorderLayout.SOUTH)
    }
}