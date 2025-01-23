package model.objects.base.item

class Armor(name: String, description: String, val absorption: Int, val negation: Double) : Item(name, description)