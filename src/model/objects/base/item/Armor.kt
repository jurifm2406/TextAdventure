package model.objects.base.item

class Armor(
    name: String = "nothing",
    description: String = "bare skin",
    var absorption: Int = 0,
    val actionPoints: Int = 1
) : Item(name, description){

    fun copy(): Armor {
        return Armor(name, description, absorption, actionPoints)
    }
}