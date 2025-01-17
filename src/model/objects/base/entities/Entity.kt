package model.objects.base.entities

import model.objects.base.Inventory
import model.objects.base.item.Armor
import model.objects.base.item.Weapon
import model.objects.world.Room
import kotlin.math.floor

abstract class Entity(
    val name: String,
    var health: Int = 20,
    val inventory: Inventory = Inventory(8),
    val weapon: Weapon = Weapon("fists", "mighty fists", 2),
    val armor: Armor = Armor("nothing", "bare skin", 0, 1.0),
    var room: Room
) {
    fun attack(target: Entity) {
        target.health -= floor((weapon.damage - target.armor.absorbtion) * target.armor.negation) as Int
    }
}