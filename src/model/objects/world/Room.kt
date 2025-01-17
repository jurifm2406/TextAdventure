package model.objects.world

import model.objects.base.Inventory
import model.objects.base.entities.Entity

class Room {
    val inventory = Inventory(64)
    val entities = mutableListOf<Entity>()
}
