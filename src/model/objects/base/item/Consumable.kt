package model.objects.base.item

class Consumable(name: String, description: String) : Item(name, description) {
    fun effect() {}
}