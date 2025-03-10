package model.objects.base.entities

import model.objects.world.Room

/**
 * The Hero class represents the player's character in the game.
 *
 * It extends the abstract Entity class, inheriting its properties and behaviors (such as health, inventory,
 * combat abilities, and room navigation), and provides hero-specific attributes like coins.
 *
 * @param name The name of the hero.
 * @param startRoom The room where the hero starts the game.
 * @param coins The initial amount of coins the hero has, defaulting to 0.
 */
class Hero(name: String, startRoom: Room, var coins: Int = 0) :
    Entity(name, 100, room = startRoom)
