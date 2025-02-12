package model.objects.base.entities

import model.objects.base.Inventory
import model.objects.base.item.Armor
import model.objects.base.item.Item
import model.objects.base.item.Weapon
import model.objects.world.CantUnequipException
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

    fun equip(item: Item) {
        if (item is Weapon) {
            if (weapon.name == "fists") {
                weapon = item
                inventory.remove(item)
            } else {
                inventory.add(weapon)
                weapon = item
                inventory.remove(item)
            }
        }

        if (item is Armor) {
            if (armor.name == "nothing") {
                armor = item
                inventory.remove(item)
            } else {
                inventory.add(armor)
                armor = item
                inventory.remove(item)
            }
        }
    }

    fun unequip(type: String) {
        if (type == "weapon") {
            if (weapon.name == "fists") {
                throw CantUnequipException(weapon.name)
            } else {
                inventory.add(weapon)
                weapon = Weapon("fists", "mighty fists", 2)
            }
        } else if (type == "armor") {
            if (armor.name == "nothing") {
                throw CantUnequipException(armor.name)
            } else {
                inventory.add(armor)
                armor = Armor("nothing", "bare skin", 0, 1.0)
            }
        }
    }
}