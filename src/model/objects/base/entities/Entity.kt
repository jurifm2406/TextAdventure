package model.objects.base.entities

import model.objects.base.Inventory
import model.objects.base.item.Armor
import model.objects.base.item.Item
import model.objects.base.item.Weapon
import model.objects.world.Room
import kotlin.math.floor

abstract class Entity(
    val name: String,
    var health: Int = 20,
    val inventory: Inventory = Inventory(8),
    var weapon: Weapon = Weapon("fists", "mighty fists", 2),
    var armor: Armor = Armor("nothing", "bare skin", 0, 1.0),
    var room: Room
) {
    fun attack(target: Entity) {
        target.health -= floor((weapon.damage - target.armor.absorption) * target.armor.negation) as Int
    }

    fun pickupItem(item: Item) {
        room.inventory.removeItem(item)
        inventory.addItem(item)
    }

    fun dropItem(item: Item) {
        inventory.removeItem(item)
        room.inventory.addItem(item)
    }
}