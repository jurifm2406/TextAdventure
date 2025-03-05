package model.objects.base.item

class Armor(
    name: String = "nothing",
    description: String = "bare skin",
    val absorption: Int = 0,
    val actionPoints: Int = 1
) : Item(name, description)