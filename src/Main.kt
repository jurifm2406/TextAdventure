import controller.Controller
import javax.swing.SwingUtilities

fun main() {
    // properties to use the mac menu bar when starting on mac
    System.setProperty("apple.laf.useScreenMenuBar", "true")
    System.setProperty("com.apple.mrj.application.apple.menu.about.name", "TextAdventure")

    // wrapper for initialization, needed to ensure thread safety
    SwingUtilities.invokeLater {
        // entry point for program
        val controller = Controller()
    }
}