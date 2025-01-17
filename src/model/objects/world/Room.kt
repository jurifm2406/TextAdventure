package model.objects.world

import model.objects.base.Inventory
import model.objects.base.entities.Entity
import java.awt.Point

class Room(val coords: Point) {
    val inventory = Inventory(64)
    val entities = mutableListOf<Entity>()
}
