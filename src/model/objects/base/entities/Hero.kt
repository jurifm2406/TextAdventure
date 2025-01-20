package model.objects.base.entities

import model.objects.world.Room

class Hero(name: String, startRoom: Room) :
    Entity(name, 100, room = startRoom) {
}