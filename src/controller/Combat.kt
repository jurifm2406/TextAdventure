package controller

import model.objects.base.entities.Entity
import model.objects.base.entities.Hero
import model.objects.base.item.Consumable
import view.View
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.round
import kotlin.random.Random
import kotlin.streams.asSequence

/**
 * The Combat class encapsulates the combat logic between a hero and an enemy.
 *
 * It handles the parsing of combat inputs (attack, defend, use item, escape, end turn, help),
 * manages the combat flow, updates action points, and applies the corresponding actions and effects.
 *
 * @param enemy The enemy entity participating in combat.
 * @param hero The hero entity controlled by the player.
 * @param view The view instance used for displaying combat messages and updates.
 */
class Combat(private val enemy: Entity, private val hero: Hero, private val view: View) {
    // Damage multipliers for the hero's and enemy's attacks.
    private var damageMultiplierHero: Double = 1.0
    private var damageMultiplierEnemy: Double = 1.0

    // Mode variable for switching to puzzle parsing
    private var mode = 0

    // Timestamps to measure the duration of puzzle response.
    private var start: Long = 0
    private var end: Long = 0

    // Puzzle string used for critical damage calculation.
    private var puzzle = ""

    // Score for evaluating the puzzle response performance.
    private var score = 1.0

    // Available action points for the hero to perform actions.
    private var actionPoints = 5

    /**
     * Parses the combat input command from the player and triggers the corresponding action.
     *
     * It also handles input when awaiting puzzle completion (mode 1).
     *
     * @param input A list of strings representing the command and its parameters.
     * @return An integer code representing the result of the command execution.
     */
    fun combatParse(input: List<String>): Int {
        if (mode == 0) {
            when (input[0].lowercase()) {
                "attack" -> return (attack(input[0]))
                "defend" -> return (defend())
                "use" -> return (useItem(input[1]))
                "escape" -> return (escape())
                "end" -> {
                    actionPoints += 5
                    enemy.tick()
                    hero.tick()
                    return enemyTurn()
                }

                "help" -> {
                    respond("available commands:")
                    respond("attack: deal damage to the enemy, costs action points according to your weapon")
                    respond("defend: reduces the enemy damage by the absorption of your armor")
                    respond("use: uses a consumable")
                    respond("escape: try to escape the combat")
                    respond("end: end your turn")
                }

                else -> respond("Invalid action.")
            }
        } else if (mode == 1) {
            // When in mode 1, the input is treated as the puzzle response for a critical attack.
            return attack(input[0])
        }
        return 0
    }

    /**
     * Handles the attack command.
     *
     * This method checks for sufficient action points
     * and depending on randomness it either initiates a critical hit opportunity by generating a puzzle (mode 0) and calculates the critical damage (mode 1).
     * It also applies weapon effects and checks if the enemy died
     *
     * @param input The command input string, or in mode 1 the player's puzzle response.
     * @return An integer code representing the outcome of the attack (e.g., enemy killed(1), attack completed(0)).
     */
    private fun attack(input: String): Int {
        // Verify sufficient action points to use the weapon.
        if (actionPoints < hero.weapon.actionPoints) {
            respond("You don't have enough action points to use that weapon")
            return 0
        }
        // Chance for a critical attack or when waiting for puzzle response (mode 1)
        if ((Random.nextInt(0, 10) < 2) || (mode == 1)) {
            if (mode == 0) {
                // Initiate critical attack: generate puzzle string and notify user.
                respond("You have the chance for critical damage")
                val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz"
                puzzle = java.util.Random().ints(Random.nextInt(5, 10).toLong(), 0, source.length)
                    .asSequence()
                    .map(source::get)
                    .joinToString("")
                respond("Type this Sequence, as fast as possible")
                respond(puzzle)
                respond("")
                start = System.currentTimeMillis()
                // Set mode to 1 to wait for the puzzle response.
                mode = 1
                return 0
            } else if (mode == 1) {
                // Process the puzzle response.
                end = System.currentTimeMillis()
                score = (needlemanWunsch(puzzle, input) - ((end - start - puzzle.length * 300.0) / 1000.0)) / 5.0
                if (hero.weapon.name != "longsword [2AP]" && score > 2.0) {
                    score = 2.0
                }
                if (score < 0.8) {
                    score = 0.8
                }
                if (hero.weapon.name == "longsword [2AP]") {
                    score += 0.3
                }
                // Check if the response was too slow and adjust the score accordingly.
                if (end - start > 4000 + puzzle.length * 500) {
                    actionPoints -= hero.weapon.actionPoints
                    respond("You were too slow, your attack deals less damage")
                    mode = 0
                    score = 0.8
                    return 0
                }
                // Display the calculated score to the player.
                val decimal = BigDecimal(score).setScale(2, RoundingMode.HALF_EVEN)
                respond("Your score is $decimal")
                mode = 0
            }
        }
        // Finalize the attack action.
        if (mode == 0) {
            actionPoints -= hero.weapon.actionPoints
            respond("You now have $actionPoints action points")
            // Special healing effect when wearing vampire's robes.
            if (hero.armor.name == "vampire's robes [1AP]") {
                hero.heal(round(hero.weapon.damage * score * 0.2).toInt())
            }
            // Attack enemy with damage scaled by the puzzle score.
            hero.attack(enemy, score)
            score = 1.0
            damageMultiplierHero = 1.0

            // Check if enemy was killed.
            if (enemy.health <= 0) {
                respond("You killed the ${enemy.name}")
                // Reward the hero with coins for killing the enemy.
                val reward = 10 + Random.nextInt(-2, 2)
                hero.coins += reward
                respond("You recieved $reward coins")
                hero.room.entities.remove(enemy)
                mode = 0
                return 1
            }
            respond("You attacked ${enemy.name}, its health is now ${enemy.health}")
            mode = 0
            return 0
        }
        return 0
    }

    /**
     * Handles the defend command.
     *
     * it subtracts action points and applies the armor's absorption
     * effect to reduce incoming enemy damage.
     *
     * @return Always returns 0 to indicate that nobody died.
     */
    private fun defend(): Int {
        if (actionPoints < hero.armor.actionPoints) {
            respond("You don't have enough action points to defend")
            return 0
        }
        hero.defend()
        actionPoints -= hero.armor.actionPoints
        respond("You now have $actionPoints action points")
        respond("You take a defensive position; the ${enemy.name} now does ${hero.armor.absorption} less damage")
        return 0
    }

    /**
     * Handles the use item command.
     *
     * Depending on the type of consumable selected (healing or damaging), this method applies its effect
     * either to the hero or to the enemy. It also checks for valid index selection and handles errors.
     *
     * @param input The index of the consumable item to be used, provided as a string.
     * @return Returns 1 if the enemy is killed, otherwise returns 0.
     */
    private fun useItem(input: String): Int {
        // Filter inventory for consumable items.
        val selection = hero.inventory.filterIsInstance<Consumable>()
        try {
            // Determine if the consumable is for healing or damaging based on its description.
            if (selection[input.toInt()].description.split(" ")[0] == "heals") {
                hero.use(selection[input.toInt()], hero)
                respond("You healed yourself, your health is now ${hero.health}")
            } else {
                hero.use(selection[input.toInt()], enemy)
                respond("You damaged the enemy, the ${enemy.name}'s health is now ${enemy.health}")
                // Check if the enemy is killed after using the consumable.
                if (enemy.health <= 0) {
                    respond("You killed the ${enemy.name}")
                    val reward = 10 + Random.nextInt(-2, 2)
                    hero.coins += reward
                    respond("You recieved $reward coins")
                    mode = 0
                    return 1
                }
            }
        } catch (e: IndexOutOfBoundsException) {
            // Handle invalid consumable index.
            respond("no consumable with that index!")
        }
        return 0
    }

    /**
     * Handles the escape command.
     *
     * The hero attempts to escape combat, consuming action points.
     * on failure, the enemy's damage multiplier is increased, making its next attack stronger.
     *
     * @return Returns 3 if escape is successful, otherwise 0.
     */
    private fun escape(): Int {
        if (actionPoints < 3) {
            respond("You don't have enough action points to escape")
            return 0
        }
        actionPoints = 0
        if (Random.nextInt(0, 2) == 0) {
            respond("You escaped")
            return 3
        }
        respond("You now have $actionPoints action points")
        respond("Your escape failed, the ${enemy.name} now does triple damage")
        damageMultiplierEnemy = 3.0
        return 0
    }

    /**
     * Processes the enemy's turn in combat.
     *
     * If the enemy is stunned, it cannot attack. Otherwise, if the hero is wearing defensive armor,
     * the enemy's damage is reduced. The enemy then attacks the hero and the method checks if the hero is dead.
     *
     * @return Returns 2 if the hero dies (unless saved by a special armor effect), otherwise 0.
     */
    private fun enemyTurn(): Int {
        if (enemy.stunned) {
            respond("the ${enemy.name} is stunned, it can't attack")
            return 0
        }
        // Apply defensive reduction if hero wears plate armor.
        if (hero.armor.name == "plate armor [2AP]") {
            damageMultiplierEnemy *= 0.8
        }
        enemy.attack(hero, damageMultiplierEnemy)
        damageMultiplierEnemy = 1.0
        // Check if the hero is dead.
        if (hero.health <= 0) {
            if (hero.armor.name == "paladin's chest plate [2AP]") {
                respond("you got saved by your chest plate, it's now broken")
                return 0
            }
            return 2
        } else {
            respond("A ${enemy.name} attacked, your health is now ${hero.health}")
            return 0
        }
    }

    /**
     * Implements the Needleman-Wunsch algorithm for sequence alignment.
     *
     * This algorithm is used to compare the puzzle sequence with the user's input in order to compute
     * a score that reflects how accurately the player responded.
     *
     * @param string1 The original puzzle string.
     * @param string2 The player's response string.
     * @return The alignment score as a Double, adjusted by a constant value.
     */
    private fun needlemanWunsch(string1: String, string2: String): Double {
        val rows = string1.length + 1
        val cols = string2.length + 1
        val matrix = Array(rows) { IntArray(cols) }
        var match: Int
        var left: Int
        var right: Int

        // Initialize the first column with gap penalties.
        for (i in 0 until rows) {
            matrix[i][0] = i * -2
        }
        // Initialize the first row with gap penalties.
        for (j in 0 until cols) {
            matrix[0][j] = j * -2
        }
        // Fill the matrix using match/mismatch and gap penalties.
        for (i in 1 until rows) {
            for (j in 1 until cols) {
                match = if (string1[i - 1] == string2[j - 1]) {
                    matrix[i - 1][j - 1] + 1
                } else {
                    matrix[i - 1][j - 1] - 1
                }
                left = matrix[i][j - 1] - 2
                right = matrix[i - 1][j] - 2
                matrix[i][j] = maxOf(left, right, match)
            }
        }
        // Return the final score adjusted by a constant.
        return matrix[rows - 1][cols - 1].toDouble() + 5.0
    }

    /**
     * Sends a response message to the view and scrolls the view to the bottom.
     *
     * @param message The message to display.
     * @param bold A flag indicating whether the message should be displayed in bold.
     */
    private fun respond(message: String?, bold: Boolean = true) {
        view.content.output.respond(message, bold)
        scrollToBottom()
    }

    /**
     * Scrolls the output view to the bottom.
     *
     * This ensures that the most recent combat messages are visible to the player.
     */
    private fun scrollToBottom() {
        view.content.scroll.verticalScrollBar.value = view.content.scroll.verticalScrollBar.maximum
    }
}
