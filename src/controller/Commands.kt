package controller

object Commands {
    val root = listOf(
        "move",
        "help",
        "inventory"
    )

    val movement = listOf(
        "north",
        "east",
        "south",
        "west"
    )

    val inventory = listOf(
        "drop",
        "take",
        "info"
    )
}