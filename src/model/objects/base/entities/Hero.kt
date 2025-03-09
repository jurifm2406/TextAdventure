package model.objects.base.entities

import model.objects.world.Room

class Hero(name: String, startRoom: Room, var coins: Int = 0) :
    Entity(name, 300, room = startRoom)
