package model

import model.objects.base.entities.Entity
import model.objects.base.item.Item
import model.objects.base.item.Weapon
import model.objects.world.Directions
import model.objects.world.Room
import model.objects.world.RoomNotThereException
import java.awt.Point
import kotlin.random.Random

/**
 * The Map class generates and manages the game map, including rooms, special rooms (start, end, chests, shop),
 * and placing items and enemies within those rooms.
 *
 * @param size The dimensions of the map represented as a Point (width x height).
 * @param floor The current floor level, for scaling the items and enemies.
 */
class Map(size: Point, floor: Int) {
    // 2D array representing the map; each cell can be a Room or null.
    var map: Array<Array<Room?>> = Array(size.x) { arrayOfNulls(size.y) }

    // The starting room where the hero begins.
    val startRoom: Room

    // The room that leads to the next floor.
    val endRoom: Room

    // A list of rooms that contain chests with items.
    val chestRooms = mutableListOf<Room>()

    // Optional shop room where the hero can buy items.
    val shopRoom: Room?

    // Scale factor based on the floor level, affecting item stats and enemy strength.
    val scale: Double = 1 + 0.4 * floor

    init {
        // --- Map Generation Setup ---
        // Calculate the middle point of the map.
        val midX = (map.size - 1) / 2
        val midY = (map[0].size - 1) / 2

        // Initialize room queue for breadth-first map generation.
        val roomQueue = mutableListOf<Room>()
        // Place the initial room at the center of the map.
        map[midX][midY] = Room(Point(midX, midY))
        roomQueue.add(map[midX][midY]!!)

        var nextRoomQueue = mutableListOf<Room>()
        var roomCount = 1
        val maxRooms = 15
        var queueStart = 0

        // --- Map Generation Loop ---
        while (roomCount < maxRooms) {
            // Iterate over current queue from the starting index.
            for (i in queueStart until roomQueue.size) {
                val room = roomQueue[i]

                // Skip adding new rooms if the current room already has more than 2 neighbors.
                if (neighbours(room.coords).filterNotNull().size > 2) {
                    continue
                }

                // Get all potential neighbour coordinates.
                val neighbourCoords = neighbourCoords(room.coords)
                neighbourCoords.shuffle() // Randomize the order to ensure varied map layouts.
                var skipNeighbours = false
                for (neighbourCoord in neighbourCoords.filterNotNull()) {
                    // Skip further neighbours if already reached maximum allowed for this room.
                    if (skipNeighbours) {
                        continue
                    }
                    // Stop if the maximum number of rooms has been placed.
                    if (roomCount == maxRooms) {
                        break
                    }
                    // Skip if the cell is already occupied.
                    if (map[neighbourCoord.x][neighbourCoord.y] != null) {
                        continue
                    }
                    // Skip if the cell has more than one neighbor.
                    if (neighbours(neighbourCoord).filterNotNull().size > 1) {
                        continue
                    }
                    // 50% chance to skip placing a room in this direction.
                    if (Random.nextInt(0, 2) == 0) {
                        continue
                    }

                    // Place a new room at the neighbour coordinate.
                    val newRoom = Room(Point(neighbourCoord))
                    map[neighbourCoord.x][neighbourCoord.y] = newRoom
                    nextRoomQueue.add(newRoom)
                    roomCount += 1

                    // If this room now has more than 2 neighbours, skip adding further neighbours.
                    if (neighbours(room.coords).filterNotNull().size > 2) {
                        skipNeighbours = true
                    }
                }
            }

            // Update the starting index for the next room queue if any new rooms were added.
            queueStart = if (nextRoomQueue.size > 0) {
                roomQueue.size
            } else {
                0
            }

            // Add the newly created rooms to the main queue and reset the nextRoomQueue.
            roomQueue += nextRoomQueue
            nextRoomQueue = mutableListOf()
        }

        // --- Special Rooms Setup ---
        // Define the starting room (center of the map).
        startRoom = map[map.size / 2][map.size / 2]!!

        // On the first floor, add random items to the starting room.
        if (floor == 0) {
            startRoom.inventory.add(Data.armors[Random.nextInt(0, Data.armors.size)])
            startRoom.inventory.add(Data.weapons[Random.nextInt(0, Data.weapons.size)])
            startRoom.inventory.add(Data.consumables[Random.nextInt(0, Data.consumables.size)])
        }

        // Create a list of all rooms to later assign special roles (end, chest, shop).
        val roomList = map.flatten().shuffled().filterNotNull().toMutableList()
        roomList.remove(startRoom)

        // Identify "special" rooms that have only one neighbor (dead-ends) as candidates.
        val specialRooms = mutableListOf<Room>()
        roomList.forEach { room ->
            if (neighbours(room.coords).filterNotNull().size == 1) {
                specialRooms.add(room)
            }
        }

        // Randomly select one special room as the end room to advance to the next floor.
        endRoom = specialRooms.random()
        roomList.remove(endRoom)
        specialRooms.remove(endRoom)

        // Randomly designate up to three special rooms as chest rooms.
        for (i in 0 until 3) {
            if (Random.nextInt(3) == 0) {
                val room = specialRooms.randomOrNull()
                if (room != null) {
                    roomList.remove(room)
                    chestRooms.add(room)
                    specialRooms.remove(room)
                }
            }
        }

        // Randomly assign a shop room from the remaining special rooms with a certain probability.
        if (Random.nextInt(3) <= 1) {
            shopRoom = specialRooms.randomOrNull()
        } else {
            shopRoom = null
        }

        roomList.remove(shopRoom)

        // --- Populate Chest Rooms with Items ---
        chestRooms.forEach {
            var item: Item
            // Add random armors to the chest room.
            for (i in 0 until Random.nextInt(1, 3)) {
                item = Data.armors[Random.nextInt(0, Data.armors.size)].copy()
                item.absorption = ((item.absorption + Random.nextInt(-2, 3)) * scale).toInt()
                map[it.coords.x][it.coords.y]!!.inventory.add(item)
            }
            // Add random weapons to the chest room.
            for (i in 0 until Random.nextInt(1, 3)) {
                item = Data.weapons[Random.nextInt(0, Data.weapons.size)].copy()
                item.damage = ((item.damage + Random.nextInt(-5, 6)) * scale).toInt()
                map[it.coords.x][it.coords.y]!!.inventory.add(item)
            }
            // Add random consumables to the chest room.
            for (i in 0 until Random.nextInt(1, 4)) {
                map[it.coords.x][it.coords.y]!!.inventory.add(
                    Data.consumables[Random.nextInt(0, Data.consumables.size)]
                )
            }
        }
        roomList.shuffle()

        // --- Place Enemies in Random Rooms ---
        for (i in 0 until Random.nextInt(4, 6)) {
            // Generate a copy of a random enemy.
            val enemy = Data.enemies[Random.nextInt(0, Data.enemies.size)].copy()
            val weapon = Weapon()
            // Scale weapon damage based on enemy health and map scale.
            weapon.damage = ((-0.1 * enemy.health + 27) * scale).toInt()
            enemy.weapon = weapon
            // Scale enemy health based on the map scale.
            enemy.health = (enemy.health * scale).toInt()
            // Assign the enemy to a random room from the list.
            enemy.room = roomList[i]
            // Add the enemy to the room's list of entities.
            map[roomList[i].coords.x][roomList[i].coords.y]!!.entities.add(enemy)
        }
    }

    /**
     * Moves an entity in the specified direction.
     *
     * This method checks for the existence of a neighbouring room in the given direction,
     * updates the entity's current and last room accordingly, and handles room non-existence
     * by throwing a RoomNotThereException with the corresponding direction.
     *
     * @param direction The direction in which to move the entity.
     * @param entity The entity that is moving.
     * @throws RoomNotThereException if there is no room in the specified direction.
     */
    fun move(direction: Int, entity: Entity) {
        val neighbours = neighbours(entity.room.coords)

        if (neighbours[direction] == null) {
            var dir = ""
            // Determine the missing direction for the error message.
            when (direction) {
                Directions.NORTH -> dir = "west"
                Directions.EAST -> dir = "south"
                Directions.SOUTH -> dir = "east"
                Directions.WEST -> dir = "north"
            }
            throw RoomNotThereException(dir)
        }

        // Update the entity's last room to its current room.
        entity.lastRoom = entity.room
        // Add the entity to the target room's entities list.
        neighbours[direction]!!.entities.add(entity)
        // Update the entity's current room reference.
        entity.room = neighbours[direction]!!
        // Remove the entity from its previous room's entities list.
        entity.room.entities.remove(entity)
    }

    /**
     * Retrieves the neighbouring rooms of the room at the given coordinates.
     *
     * The neighbours are returned in an array with fixed indices corresponding to:
     * NORTH, EAST, SOUTH, and WEST.
     *
     * @param roomCoords The coordinates of the current room.
     * @return An array of neighbouring rooms. A null value indicates no room exists in that direction.
     */
    fun neighbours(roomCoords: Point): Array<Room?> {
        val neighbors = arrayOfNulls<Room?>(4)

        // Check for a neighbour to the NORTH.
        if (roomCoords.y > 0) {
            neighbors[Directions.NORTH] = map[roomCoords.x][roomCoords.y - 1]
        }
        // Check for a neighbour to the EAST.
        if (roomCoords.x < map.size - 1) {
            neighbors[Directions.EAST] = map[roomCoords.x + 1][roomCoords.y]
        }
        // Check for a neighbour to the SOUTH.
        if (roomCoords.y < map[0].size - 1) {
            neighbors[Directions.SOUTH] = map[roomCoords.x][roomCoords.y + 1]
        }
        // Check for a neighbour to the WEST.
        if (roomCoords.x > 0) {
            neighbors[Directions.WEST] = map[roomCoords.x - 1][roomCoords.y]
        }

        return neighbors
    }

    /**
     * Generates the coordinates of neighbouring cells for a given room.
     *
     * This method returns an array of Points representing the coordinates in the
     * NORTH, EAST, SOUTH, and WEST directions.
     *
     * @param roomCoords The coordinates of the current room.
     * @return An array of neighbouring coordinates. A null value indicates that the direction is invalid.
     */
    private fun neighbourCoords(roomCoords: Point): Array<Point?> {
        val neighbourCoord = arrayOfNulls<Point?>(4)

        // Calculate coordinate for the NORTH direction.
        if (roomCoords.y > 0) {
            neighbourCoord[Directions.NORTH] = Point(roomCoords.x, roomCoords.y - 1)
        }
        // Calculate coordinate for the EAST direction.
        if (roomCoords.x < map.size - 1) {
            neighbourCoord[Directions.EAST] = Point(roomCoords.x + 1, roomCoords.y)
        }
        // Calculate coordinate for the SOUTH direction.
        if (roomCoords.y < map[0].size - 1) {
            neighbourCoord[Directions.SOUTH] = Point(roomCoords.x, roomCoords.y + 1)
        }
        // Calculate coordinate for the WEST direction.
        if (roomCoords.x > 0) {
            neighbourCoord[Directions.WEST] = Point(roomCoords.x - 1, roomCoords.y)
        }

        return neighbourCoord
    }
}
