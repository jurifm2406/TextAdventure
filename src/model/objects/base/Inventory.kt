package model.objects.base

import model.objects.base.item.Item

class Inventory(val size: Int) {
    private val content: MutableList<Item> = mutableListOf()

    fun addItem(item: Item) {
        if (content.size >= size) {
            throw InventoryFullException()
        }

        content.add(item)
    }
}