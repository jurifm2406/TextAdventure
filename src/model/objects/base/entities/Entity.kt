package model.objects.base.entities

import model.objects.base.Inventory
import model.objects.base.item.Armor
import model.objects.base.item.Consumable
import model.objects.base.item.Item
import model.objects.base.item.Weapon
import model.objects.world.CantUnequipException
import model.objects.world.Room
import java.awt.Point

/**
 * The Entity class serves as an abstract base for all characters and creatures in the game,
 * such as heroes and enemies. health, inventory management, combat actions, and room navigation.
 *
 * @property name The name of the entity.
 * @property maxHealth The maximum health points of the entity.
 * @property inventory The inventory holding items available to the entity.
 * @property weapon The weapon currently equipped by the entity.
 * @property armor The armor currently equipped by the entity.
 * @property room The room in which the entity is currently located.
 * @property effects A list of active effects (functions) applied to the entity.
 * @property stunned Flag indicating whether the entity is stunned.
 * @property absorption The absorption value used to mitigate incoming damage.
 */
abstract class Entity(
    val name: String,
    val maxHealth: Int = 20,
    val inventory: Inventory = Inventory(8),
    var weapon: Weapon = Weapon(),
    var armor: Armor = Armor(),
    var room: Room,
    val effects: MutableList<(Entity) -> Unit> = mutableListOf(),
    var stunned: Boolean = false,
    var absorption: Int = 0
) {
    var health: Int = maxHealth
    var lastRoom = room

    /**
     * dropping inventory to room and removing all references so the garbage collector takes care of the dead enemy
     */
    private fun die() {
        // hardcoded implementation of the resurrect function of the paladin's chest plate
        if (armor.name == "paladin's chest plate [2AP]") {
            health = 40
            armor = Armor()
        }
        this.inventory.forEach { room.inventory.add(it) }

        room = Room(Point(-1, -1))
        room.entities.remove(this)
    }

    /**
     * apply effects caused by weapons/armors/consumables
     */
    fun tick() {
        stunned = false
        effects.forEach {
            it(this)
            effects.remove(it)
        }
    }

    /**
     * adding armor absorption to entity absorption to be used in next attack
     */
    fun defend() {
        absorption += armor.absorption
    }

    /**
     * heal with respect to max health
     */
    fun heal(amount: Int) {
        health += amount
        health = if (health > maxHealth) maxHealth else health
    }

    /**
     * damage with possible invocation of death when health drops below zero through damage
     */
    fun damage(amount: Int) {
        health -= amount
        if (health < 0) die()
    }

    /**
     * attack other entity, calculating in attack multiplier and target absorption
     */
    fun attack(target: Entity, multiplier: Double) {
        var damage = (weapon.damage * multiplier - target.absorption).toInt()
        target.absorption = 0
        damage = if (damage < 0) 0 else damage
        weapon.effects.forEach { target.effects.add(it) }
        target.damage(damage)
    }

    /**
     * pick up item from current room
     */
    fun pickup(item: Item) {
        room.inventory.remove(item)
        inventory.add(item)
        // equipping immediately if no weapon or armor is equipped
        if (weapon.name == "fists" && item is Weapon) {
            equip(item)
        }
        if (armor.name == "nothing" && item is Armor) {
            equip(item)
        }
    }

    /**
     * drop item to room
     */
    fun drop(item: Item) {
        inventory.remove(item)
        room.inventory.add(item)
    }

    /**
     * equip item from inventory
     * if no weapon or armor is equipped, fists or nothing is replaced
     */
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

    /**
     * unequip weapon or armor (replacing it with default)
     */
    fun unequip(type: String) {
        if (type == "weapon") {
            if (weapon.name == "fists") {
                throw CantUnequipException(weapon.name)
            } else {
                inventory.add(weapon)
                weapon = Weapon()
            }
        } else if (type == "armor") {
            if (armor.name == "nothing") {
                throw CantUnequipException(armor.name)
            } else {
                inventory.add(armor)
                armor = Armor()
            }
        }
    }

    /**
     * use consumable
     */
    fun use(consumable: Consumable, target: Entity) {
        consumable.effect(target)

        if (consumable.destroyOnUse) {
            inventory.remove(consumable)
        }
    }
}