package model.objects.base.item

/**
 * The Item class serves as an abstract base for all items in the game.
 *
 * It encapsulates common properties such as the item's name and a brief description.
 * Specific types of items (e.g., weapons, armors, consumables) will extend this class.
 *
 * @param name The name of the item.
 * @param description A brief description of the item.
 */
abstract class Item(val name: String, val description: String)