package model

import model.objects.base.entities.Entity
import model.objects.world.Directions
import model.objects.world.Room
import model.objects.world.RoomNotThereException
import java.awt.Point
import kotlin.random.Random


class Map(size: Point) {
    var map: Array<Array<Room?>> = Array(size.x) { arrayOfNulls(size.y) }
    val startRoom: Room

    init {
        // variables for map generation
        val midX = (map.size - 1) / 2
        val midY = (map[0].size - 1) / 2

        val roomQueue = mutableListOf<Room>()
        map[midX][midY] = Room(Point(midX, midY))
        roomQueue.add(map[midX][midY]!!)

        var nextRoomQueue = mutableListOf<Room>()

        var roomCount = 1
        val maxRooms = 15
        var queueStart = 0

        // generates map
        while (roomCount < maxRooms) {
            for (i in queueStart..<roomQueue.size) {
                val room = roomQueue[i]

                if (neighbours(room.coords).filterNotNull().size > 2) {
                    continue
                }

                val neighbourCoords = neighbourCoords(room.coords)
                neighbourCoords.shuffle()
                var skipNeighbours = false
                for (neighbourCoord in neighbourCoords.filterNotNull()) {
                    // skip if max neighbours already reached
                    if (skipNeighbours) {
                        continue
                    }
                    // give up if maxRooms reached
                    if (roomCount == maxRooms) {
                        break
                    }
                    // give up if cell is not empty
                    if (map[neighbourCoord.x][neighbourCoord.y] != null) {
                        continue
                    }
                    // give up if cell has more than one neighbour
                    if (neighbours(neighbourCoord).filterNotNull().size > 1) {
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
                    if (neighbours(room.coords).filterNotNull().size > 2) {
                        skipNeighbours = true
                    }
                }
            }

            queueStart = if (nextRoomQueue.size > 0) {
                roomQueue.size
            } else {
                0
            }

            roomQueue += nextRoomQueue
            nextRoomQueue = mutableListOf()
        }
        startRoom = map[map.size / 2][map.size / 2]!!

        startRoom.inventory.add(Data.armors[Random.nextInt(Data.armors.size)])
        startRoom.inventory.add(Data.weapons[Random.nextInt(Data.weapons.size)])



        // create room list to place items and entities in
        val roomList = map.flatten().shuffled().filterNotNull().toMutableList()
        roomList.remove(startRoom)

        val endRooms = mutableListOf<Room>()

        roomList.forEach { room ->
            if (neighbours(room.coords).filterNotNull().size == 1) {
                endRooms.add(room)
            }
        }

        // add weapons to random number of rooms
        for (i in 0..<Random.nextInt(4, 6)) {
            map[roomList[i].coords.x][roomList[i].coords.y]!!.inventory.add(
                Data.weapons[Random.nextInt(
                    0,
                    Data.weapons.size
                )]
            )
        }
        roomList.shuffle()
        // add armour to random number of rooms
        for (i in 0..<Random.nextInt(4, 6)) {
            map[roomList[i].coords.x][roomList[i].coords.y]!!.inventory.add(
                Data.armors[Random.nextInt(
                    0,
                    Data.armors.size
                )]
            )
        }
        roomList.shuffle()
        // add enemies to random number of rooms
        for (i in 0..<Random.nextInt(4, 6)) {
            map[roomList[i].coords.x][roomList[i].coords.y]!!.entities.add(
                Data.enemies[Random.nextInt(
                    0,
                    Data.enemies.size
                )]
            )
            map[roomList[i].coords.x][roomList[i].coords.y]!!.entities[0].armor =
                Data.armors[Random.nextInt(0, Data.armors.size)]
            map[roomList[i].coords.x][roomList[i].coords.y]!!.entities[0].weapon =
                Data.weapons[Random.nextInt(0, Data.weapons.size)]
            map[roomList[i].coords.x][roomList[i].coords.y]!!.entities[0].room = roomList[i]
        }
        for (i in 0..<map.size) {
            for (j in 0..<map[0].size) {
                if (map[i][j] != null) {
                    for (k in 0..<map[i][j]!!.inventory.size) {
                        print(map[i][j]!!.inventory[k].name)
                    }
                    for (l in 0..<map[i][j]!!.entities.size) {
                        print(map[i][j]!!.entities[l].name)
                    }

                }

            }
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

            throw RoomNotThereException(dir)
        }

        entity.room.entities.remove(entity)
    }

    fun neighbours(roomCoords: Point): Array<Room?> {
        val neighbors = arrayOfNulls<Room?>(4)

        if (roomCoords.y > 0) {
            neighbors[Directions.NORTH] = map[roomCoords.x][roomCoords.y - 1]
        }

        if (roomCoords.x < map.size - 1) {
            neighbors[Directions.EAST] = map[roomCoords.x + 1][roomCoords.y]
        }

        if (roomCoords.y < map[0].size - 1) {
            neighbors[Directions.SOUTH] = map[roomCoords.x][roomCoords.y + 1]
        }

        if (roomCoords.x > 0) {
            neighbors[Directions.WEST] = map[roomCoords.x - 1][roomCoords.y]
        }

        return neighbors
    }

    private fun neighbourCoords(roomCoords: Point): Array<Point?> {
        val neighbourCoord = arrayOfNulls<Point?>(4)

        if (roomCoords.y > 0) {
            neighbourCoord[Directions.NORTH] = Point(roomCoords.x, roomCoords.y - 1)
        }

        if (roomCoords.x < map.size - 1) {
            neighbourCoord[Directions.EAST] = Point(roomCoords.x + 1, roomCoords.y)
        }

        if (roomCoords.y < map[0].size - 1) {
            neighbourCoord[Directions.SOUTH] = Point(roomCoords.x, roomCoords.y + 1)
        }

        if (roomCoords.x > 0) {
            neighbourCoord[Directions.WEST] = Point(roomCoords.x - 1, roomCoords.y)
        }

        return neighbourCoord
    }
}