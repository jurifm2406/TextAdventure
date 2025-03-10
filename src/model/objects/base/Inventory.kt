package model.objects.base

import model.objects.base.item.Armor
import model.objects.base.item.Consumable
import model.objects.base.item.Item
import model.objects.base.item.Weapon
import model.objects.world.ItemNotThereException

/**
 * holds items for rooms or entities
 * inherits from arraylist as it largely is one
 */
class Inventory(var maxSize: Int) : ArrayList<Item>() {
    /**
     * add item with respect to max size
     */
    override fun add(item: Item): Boolean {
        if (size >= maxSize) {
            throw InventoryFullException()
        }

        return super.add(item)
    }

    /**
     * remove item from inventory, overridden to throw itemnotthereexception
     */
    override fun remove(item: Item): Boolean {
        if (!contains(item)) {
            throw ItemNotThereException(item.name)
        }
        return super.remove(item)
    }

    /**
     * export inventory information to be used by inspect() and other methods
     */
    fun export(): Array<Array<Array<String>>> {
        val export = mutableListOf<Array<Array<String>>>()

        if (filterIsInstance<Weapon>().isNotEmpty()) {
            val exportWeapons = mutableListOf<Array<String>>()
            exportWeapons.add(arrayOf("WEAPONS", "", ""))
            exportWeapons.add(arrayOf("ID", "NAME", "DAMAGE"))
            filterIsInstance<Weapon>().forEachIndexed { i, weapon ->
                exportWeapons.add(arrayOf(i.toString(), weapon.name, weapon.damage.toString()))
            }
            export.add(exportWeapons.toTypedArray())
        }

        if (filterIsInstance<Armor>().isNotEmpty()) {
            val exportArmors = mutableListOf<Array<String>>()
            exportArmors.add(arrayOf("ARMORS", "", ""))
            exportArmors.add(arrayOf("ID", "NAME", "ABS"))
            filterIsInstance<Armor>().forEachIndexed { i, armor ->
                exportArmors.add(
                    arrayOf(
                        i.toString(),
                        armor.name,
                        armor.absorption.toString(),
                    )
                )
            }
            export.add(exportArmors.toTypedArray())
        }

        if (filterIsInstance<Consumable>().isNotEmpty()) {
            val exportConsumables = mutableListOf<Array<String>>()
            exportConsumables.add(arrayOf("CONSUMABLES", "", ""))
            exportConsumables.add(arrayOf("ID", "NAME", "EFFECT"))
            filterIsInstance<Consumable>().forEachIndexed { i, consumable ->
                exportConsumables.add(arrayOf(i.toString(), consumable.name, consumable.description))
            }
            export.add(exportConsumables.toTypedArray())
        }

        return export.toTypedArray()
    }
}