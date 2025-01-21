package model

import model.objects.base.Inventory
import model.objects.base.entities.Enemy

object Data {

    val enemies: Array<Enemy> = arrayOf(Enemy("Orc", 150, Inventory(10)))
}