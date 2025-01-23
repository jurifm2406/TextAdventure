package model.objects.world

class RoomNotThereException(direction: String) : Exception("there's no room to the $direction")