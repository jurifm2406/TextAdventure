package model.objects.base

import model.objects.base.item.Item
import model.objects.world.ItemNotThereException

class Inventory(val size: Int) {
    private val _content: MutableList<Item> = mutableListOf()
    val content: List<Item> get() = _content

    fun addItem(item: Item) {
        if (_content.size >= size) {
            throw InventoryFullException()
        }

        _content.add(item)
    }

    fun removeItem(item: Item) {
        if (!_content.contains(item)) {
            throw ItemNotThereException(item.name)
        }

        _content.remove(item)
    }
}