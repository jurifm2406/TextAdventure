package model

import model.objects.base.entities.Hero
import java.awt.Point
import javax.swing.table.DefaultTableModel
/**
 * The main game model that manages the hero, map, and game state.
 *
 * This class initializes the game world, including the hero and the map.
 * It also tracks the current floor level and provides an information model for the UI.
 *
 * @param heroName The name of the hero character controlled by the player.
 */
class Model(heroName: String) {
    val mapSize = Point(9, 9)
    var map = Map(mapSize, 0)
    var hero = Hero(heroName, map.startRoom)
    var floor = 0
    val infoModel = DefaultTableModel(3, 3)
}