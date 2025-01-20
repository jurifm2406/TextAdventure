package model

import model.objects.base.entities.Enemy
import model.objects.base.Inventory

object Data {

    val enemies: Array<Enemy> = (Enemy("Orc",150, Inventory(10), ))
}