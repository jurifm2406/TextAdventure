package model.objects.base.entities

import model.objects.base.Inventory
import model.objects.base.item.Armor
import model.objects.base.item.Weapon
import model.objects.world.Room
import java.awt.Point

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