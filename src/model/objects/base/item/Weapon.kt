package model.objects.base.item

import model.objects.base.entities.Entity

/**
 * uses a list of lambdas to store possibly multiple weapon effects
 * this is kind of a janky workaround and only works for some effects
 * action points are used up when attacking in combat, "heavier" weapons need more points
 * @param name the name of the weapon
 * @param description description of the weapon, includes effect
 * @param damage damage the weapon deals to attacked enemies
 * @param actionPoints the amount of actionPoints the weapon consumes upon attack
 * @param effects the effects the weapon applies to enemies (or self) upon attack
 */
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
