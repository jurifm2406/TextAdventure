package model.objects.base.item

import model.objects.base.entities.Entity

/**
 * @param name the name of the consumable
 * @param description description of consumable, describes the effect
 * @param effect the effect of the consumable, represented by a lambda with the target as parameter
 */
class Consumable(name: String, description: String, val effect: (Entity) -> Unit, val destroyOnUse: Boolean = true) :
    Item(name, description) {

    fun copy(): Consumable {
        return Consumable(name, description, effect, destroyOnUse)
    }
}