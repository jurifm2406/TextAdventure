package model.objects.base.entities

import model.objects.base.Inventory
import model.objects.base.item.Armor
import model.objects.base.item.Weapon
import model.objects.world.Room
import java.awt.Point

/**
 * The Enemy class represents an enemy character in the game. It extends the base Entity class,
 * providing enemy-specific properties and behaviors. This class is primarily used to generate
 * enemy instances for the game map.
 *
 * @param name The enemy's name.
 * @param health The enemy's health, defaulting to 10.
 * @param inventory The enemy's inventory with a default capacity of 5 items.
 * @param weapon The enemy's equipped weapon.
 * @param armor The enemy's equipped armor.
 * @param room The room where the enemy is initially located; defaults to an off-map location.
 */
class Enemy(
    name: String = "",
    health: Int = 10,
    inventory: Inventory = Inventory(5),
    weapon: Weapon = Weapon(),
    armor: Armor = Armor(),
    room: Room = Room(Point(-1, -1))
) :
    Entity(name, health, inventory, weapon, armor, room) {

    /**
     * copy enemy for map generation
     */
    fun copy(): Enemy {
        return Enemy(
            name,
            health,
            inventory,
            weapon,
            armor,
            room
        )
    }
}