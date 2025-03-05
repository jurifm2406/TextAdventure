package model.objects.base.item

class Weapon(
    name: String = "fists",
    description: String = "mighty fists",
    val damage: Int = 2,
    val actionPoints: Int = 1
) : Item(name, description)