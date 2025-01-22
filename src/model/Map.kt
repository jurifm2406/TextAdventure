package model

import model.objects.base.entities.Entity
import model.objects.world.Directions
import model.objects.world.Room
import model.objects.world.RoomNotThereException
import java.awt.Point
import kotlin.random.Random

class Map(size: Point) {
    val map: Array<Array<Room?>> = Array(size.x) { arrayOfNulls(size.y) }
    val startRoom: Room

    init {
        // variables for map generation
        val midX = (map.size - 1) / 2
        val midY = (map[0].size - 1) / 2

        val roomQueue = mutableListOf<Room>()
        var currentRoom = Room(Point(midX, midY))
        map[midX][midY] = currentRoom
        roomQueue.add(currentRoom)

        var nextRoomQueue = mutableListOf<Room>()

        var roomCount = 1
        val maxRooms = 15

        // generates map
        while (roomCount < maxRooms) {
            for (i in 0..<roomQueue.size) {
                val room = roomQueue[i]
                val neighbourCoords = neighbourCoords(room.coords)
                neighbourCoords.shuffle()
                var skipNeighbours = false
                for (neighbourCoord in neighbourCoords) {
                    // skip if max neighbours already reached
                    if (skipNeighbours) {
                        continue
                    }
                    // give up if maxRooms reached
                    if (roomCount == maxRooms) {
                        break
                    }
                    // check if there is neighbouring cell
                    if (neighbourCoord == null) {
                        continue
                    }
                    // give up if cell is not empty
                    if (map[neighbourCoord.x][neighbourCoord.y] != null) {
                        continue
                    }
                    // give up if cell has more than one neighbour
                    val subNeighbours = neighbours(neighbourCoord)
                    if (subNeighbours.filterNotNull().size > 1) {
                        continue
                    }
                    // 50% chance to give up
                    if (Random.nextInt(0, 2) == 0) {
                        continue
                    }

                    // place room if not given up
                    val newRoom = Room(Point(neighbourCoord))
                    map[neighbourCoord.x][neighbourCoord.y] = newRoom
                    nextRoomQueue.add(newRoom)
                    roomCount += 1

                    // skip rest of neighbours if already 3 rooms placed
                    if (neighbours(room.coords).size > 2) {
                        skipNeighbours = true
                    }
                }
            }

            roomQueue += nextRoomQueue
            nextRoomQueue = mutableListOf()
        }
        startRoom = map[map.size / 2][map.size / 2]!!

        // create room list to place items and entities in
        val roomList = map.flatten().shuffled().toMutableList().filterNotNull().toMutableList()
        roomList.remove(startRoom)

        val endRooms = mutableListOf<Room>()

        roomList.forEach { room ->
            if (neighbours(room.coords).filterNotNull().size == 1) {
                endRooms.add(room)
                println("${room.coords.x}, ${room.coords.y}")
            }
        }

        // add enemies to certain number of rooms
        for (i in 0..Random.nextInt(4, 6)) {

        }

    }

    fun move(direction: Int, entity: Entity) {
        val neighbours = neighbours(entity.room.coords)

        if (direction == Directions.NORTH && neighbours[Directions.NORTH] != null) {
            neighbours[Directions.NORTH]!!.entities.add(entity)
            entity.room = neighbours[Directions.NORTH]!!
        } else if (direction == Directions.EAST && neighbours[Directions.EAST] != null) {
            neighbours[Directions.EAST]!!.entities.add(entity)
            entity.room = neighbours[Directions.EAST]!!
        } else if (direction == Directions.SOUTH && neighbours[Directions.SOUTH] != null) {
            neighbours[Directions.SOUTH]!!.entities.add(entity)
            entity.room = neighbours[Directions.SOUTH]!!
        } else if (direction == Directions.WEST && neighbours[Directions.WEST] != null) {
            neighbours[Directions.WEST]!!.entities.add(entity)
            entity.room = neighbours[Directions.WEST]!!
        } else {
            var dir = ""

            when (direction) {
                Directions.NORTH -> dir = "west"
                Directions.EAST -> dir = "south"
                Directions.SOUTH -> dir = "east"
                Directions.WEST -> dir = "north"
            }

            throw RoomNotThereException("There's no room to the $dir!")
        }

        entity.room.entities.remove(entity)
    }

    private fun neighbours(roomCoords: Point): Array<Room?> {
        val neighbors = arrayOfNulls<Room?>(4)

        val x = roomCoords.x
        val y = roomCoords.y

        if (y > 0) {
            neighbors[Directions.NORTH] = map[x][y - 1]
        }

        if (x < map.size - 1) {
            neighbors[Directions.EAST] = map[x + 1][y]
        }

        if (y < map[0].size - 1) {
            neighbors[Directions.SOUTH] = map[x][y + 1]
        }

        if (x > 0) {
            neighbors[Directions.WEST] = map[x - 1][y]
        }

        return neighbors
    }

    private fun neighbourCoords(roomCoords: Point): Array<Point?> {
        val neighbourCoord = arrayOfNulls<Point?>(4)

        val x = roomCoords.x
        val y = roomCoords.y

        if (y > 0) {
            neighbourCoord[Directions.NORTH] = Point(x, y - 1)
        }

        if (x < map.size - 1) {
            neighbourCoord[Directions.EAST] = Point(x + 1, y)
        }

        if (y < map[0].size - 1) {
            neighbourCoord[Directions.SOUTH] = Point(x, y + 1)
        }

        if (x > 0) {
            neighbourCoord[Directions.WEST] = Point(x - 1, y)
        }

        return neighbourCoord
    }
}