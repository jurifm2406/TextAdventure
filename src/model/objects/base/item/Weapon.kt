package model.objects.base.item

import model.objects.base.entities.Entity
class Weapon(
    name: String = "fists",
    description: String = "mighty fists",
    var damage: Int = 2,
    val actionPoints: Int = 1,
    val effects: MutableList<(Entity) -> Unit> = mutableListOf()
) : Item(name, description) {

    fun copy(): Weapon {
        return Weapon(
            name,
            description,
            damage,
            actionPoints,
            effects.toMutableList() // This creates a new list to avoid referencing the same list
        )
    }
}
