package model.objects.world

class ItemNotThereException(itemName: String) : Exception("$itemName doesn't exist in this room")