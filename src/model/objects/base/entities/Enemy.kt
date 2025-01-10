package model.objects.base.entities

import model.objects.base.Inventory
import model.objects.base.item.Armor
import model.objects.base.item.Weapon

class Enemy(name: String, health: Int, inventory: Inventory, weapon: Weapon, armor: Armor) :
    Entity(name, health, inventory, weapon, armor) {
}