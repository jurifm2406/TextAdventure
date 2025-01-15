import controller.Controller
import javax.swing.SwingUtilities

fun main() {
    System.setProperty("apple.laf.useScreenMenuBar", "true")
    System.setProperty("com.apple.mrj.application.apple.menu.about.name", "TextAdventure")

    SwingUtilities.invokeLater {
        val controller = Controller()
    }
}