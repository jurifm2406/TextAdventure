package model.objects.base.item

class Weapon(name: String, description: String, val damage: Int, val actionPoint: Int = 1) : Item(name, description)