package model.objects.base

import model.objects.base.item.Armor
import model.objects.base.item.Consumable
import model.objects.base.item.Item
import model.objects.base.item.Weapon
import model.objects.world.ItemNotThereException

class Inventory(var maxSize: Int) : ArrayList<Item>() {
    override fun add(item: Item): Boolean {
        if (size >= maxSize) {
            throw InventoryFullException()
        }

        return super.add(item)
    }

    override fun remove(item: Item): Boolean {
        if (!contains(item)) {
            throw ItemNotThereException(item.name)
        }
        return super.remove(item)
    }

    fun export() {
        val export = mutableListOf<String>()

        export.add("WEAPONS")
        export.add("ID  NAME                DAMAGE          ")
        filterIsInstance<Weapon>().forEachIndexed { i, weapon ->
            export.add("$i  ${weapon.name}      ${weapon.damage}")
        }
        export.add("ARMORS")
        export.add("ID  NAME        ABS NEG ")
        filterIsInstance<Armor>().forEachIndexed { i, armor ->
            export.add("$i  ${armor.name}       ${armor.absorption}     ${armor.negation}")
        }
        export.add("ITEMS")
        export.add("ID  NAME        EFFECT")
        filterIsInstance<Consumable>().forEachIndexed { i, item ->
            export.add("$i  ${item.name}        ${item.description}")

        }
    }
}