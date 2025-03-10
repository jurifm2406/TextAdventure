package model.objects.base.item

import model.objects.base.entities.Entity

/**
 * uses a lambda function with a target (Entity) to store it's effect
 */
class Consumable(name: String, description: String, val effect: (Entity) -> Unit, val destroyOnUse: Boolean = true) :
    Item(name, description) {

    fun copy(): Consumable {
        return Consumable(name, description, effect, destroyOnUse)
    }
}