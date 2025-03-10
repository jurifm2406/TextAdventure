package model.objects.base.item

/**
 * action points are required during combat, better armor needs more action points
 *
 * @param name the name of the armor
 * @param description description of the armor, includes effects
 * @param absorption the amount of damage the armor blocks
 * @param actionPoints the amount of actionPoints the armor takes to block
 */
class Armor(
    name: String = "nothing",
    description: String = "bare skin",
    var absorption: Int = 0,
    val actionPoints: Int = 1
) : Item(name, description) {
    fun copy(): Armor {
        return Armor(name, description, absorption, actionPoints)
    }
}