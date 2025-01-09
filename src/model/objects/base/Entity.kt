package model.objects.base

import model.objects.base.item.Armor
import model.objects.base.item.Weapon
import kotlin.math.floor

class Entity(
    val name: String,
    var health: Int = 20,
    val inventory: Inventory = Inventory(8),
    val weapon: Weapon = Weapon("Fists", "Mighty fists", 2),
    val armor: Armor = Armor("Nothing", "Bare skin", 0, 1.0)
) {
    fun attack(target: Entity) {
        target.health -= floor((weapon.damage - target.armor.absorbtion) * target.armor.negation) as Int
    }
}