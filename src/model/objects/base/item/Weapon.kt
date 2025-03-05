package model.objects.base.item

import model.objects.base.entities.Entity

class Weapon(
    name: String = "fists",
    description: String = "mighty fists",
    val damage: Int = 2,
    val actionPoints: Int = 1,
    val effects: MutableList<(Entity) -> Unit> = mutableListOf()
) : Item(name, description)