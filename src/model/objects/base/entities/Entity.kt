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
        var damage = weapon.damage - target.armor.absorption
        if (damage < 0){
            damage = 0
        }
        target.health -= floor(damage * target.armor.negation).toInt()
    }

    fun pickup(item: Item) {
        room.inventory.remove(item)
        inventory.add(item)
    }

    fun drop(item: Item) {
        inventory.remove(item)
        room.inventory.add(item)
    }
}