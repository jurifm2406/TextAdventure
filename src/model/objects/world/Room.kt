package model.objects.world

import model.objects.base.Inventory
import model.objects.base.entities.Entity
import java.awt.Point
/**
 * Represents a room in the game world.
 *
 * Each room has a set of coordinates, an inventory to store items,
 * and a list of entities (such as players or enemies) that may occupy the room.
 *
 * @param coords The position of the room within the game world.
 */
class Room(val coords: Point) {
    val inventory = Inventory(64)
    val entities = mutableListOf<Entity>()
}
