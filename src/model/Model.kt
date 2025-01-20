package model

import model.objects.base.entities.Hero
import javax.swing.table.DefaultTableModel

class Model(heroName: String) {
    val mapModel = DefaultTableModel(9, 9)
    val map = Map(9)
    val hero = Hero(heroName, map.startRoom)
    var floor = 0
    val infoModel = DefaultTableModel(3, 3)
}