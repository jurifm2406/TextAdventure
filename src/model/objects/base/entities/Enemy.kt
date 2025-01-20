package model.objects.base.entities

import model.objects.base.Inventory
import model.objects.base.item.Armor
import model.objects.base.item.Weapon
import model.objects.world.Room

class Enemy(name: String, health: Int, inventory: Inventory, weapon: Weapon, armor: Armor, room: Room) :
    Entity(name, health, inventory, weapon, armor, room)