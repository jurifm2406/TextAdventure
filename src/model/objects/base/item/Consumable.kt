package model.objects.base.item

import model.objects.base.entities.Entity

class Consumable(name: String, description: String, val effect: (Entity) -> Unit, val destroyOnUse: Boolean = true) :
    Item(name, description)