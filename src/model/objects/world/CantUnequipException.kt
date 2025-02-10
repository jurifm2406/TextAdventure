package model.objects.world

class CantUnequipException(name: String) : Exception("you can't unequip your $name")