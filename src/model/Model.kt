package model

import model.objects.base.entities.Hero
import java.awt.Point
import javax.swing.table.DefaultTableModel

class Model(heroName: String) {
    val mapSize = Point(9, 9)
    val map = Map(mapSize, 0)
    val hero = Hero(heroName, map.startRoom)
    var floor = 0
    val infoModel = DefaultTableModel(3, 3)
}