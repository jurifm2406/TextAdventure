package model

import model.objects.world.Directions
import model.objects.world.Room
import java.awt.Point
import kotlin.random.Random

class Map(size: Int) {
    val map: Array<Array<Room?>> = Array(size) { arrayOfNulls(size) }

    init {
        // variables for map generation
        val midY = (map[0].size - 1) / 2
        val midX = (map.size - 1) / 2

        var roomQueue = mutableListOf<Room>()
        var currentRoom = Room(Point(midX, midY))
        map[midX][midY] = currentRoom
        roomQueue.add(currentRoom)

        var nextRoomQueue = mutableListOf<Room>()

        var roomCount = 1
        val maxRooms = 10

        // generates map
        while (roomCount < maxRooms) {
            for (room in roomQueue) {
                val neighbourCoords = neighbourCoords(room.coords)
                for (neighbourCoord in neighbourCoords) {
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
                }
            }
            roomQueue = nextRoomQueue
            nextRoomQueue = mutableListOf()
        }
    }

    fun move(direction: Int) {

    }

    fun neighbours(roomCoords: Point): Array<Room?> {
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

    fun neighbourCoords(roomCoords: Point): Array<Point?> {
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

    fun export(): Array<Array<String>> {
        return map.map {
            it.map { item ->
                if (item is Room) "x" else ""
            }.toTypedArray()
        }.toTypedArray()
    }
}