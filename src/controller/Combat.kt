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


class Combat(private val enemy: Entity, private val hero: Hero, private val view: View) {
    private var damageMultiplierHero: Double = 1.0
    private var damageMultiplierEnemy: Double = 1.0
    private var mode = 0
    private var start: Long = 0
    private var end: Long = 0
    private var puzzle = ""
    private var score = 1.0
    private var actionPoints = 5

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
            return attack(input[0])
        }
        return 0
    }

    private fun attack(input: String): Int {
        if (actionPoints < hero.weapon.actionPoints) {
            respond("You don't have enough action points to use that weapon")
            return 0
        }
        if ((Random.nextInt(0, 10) < 2) || (mode == 1)) {
            if (mode == 0) {
                respond("You have the chance for critical damage")
                // generate puzzle
                val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz"
                puzzle = java.util.Random().ints(Random.nextInt(5, 10).toLong(), 0, source.length)
                    .asSequence()
                    .map(source::get)
                    .joinToString("")
                respond("Type this Sequence, as fast as possible")
                respond(puzzle)
                respond("")
                start = System.currentTimeMillis()
                // call view for
                mode = 1

                return 0
            } else if (mode == 1) {
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

                if (end - start > 4000 + puzzle.length * 500) {
                    actionPoints -= hero.weapon.actionPoints
                    respond("You were too slow, your attack deals less damage")
                    mode = 0
                    score = 0.8
                    return 0
                }
                val decimal = BigDecimal(score).setScale(2, RoundingMode.HALF_EVEN)
                respond("Your score is $decimal")
                mode = 0
            }
        }

        if (mode == 0) {
            actionPoints -= hero.weapon.actionPoints
            respond("You now have $actionPoints action points")

            if (hero.armor.name == "vampire's robes [1AP]") {
                hero.heal(round(hero.weapon.damage * score * 0.2).toInt())
            }

            hero.attack(enemy, (score))
            score = 1.0

            damageMultiplierHero = 1.0
            if (enemy.health <= 0) {
                respond("You killed the ${enemy.name}")
                // add coins for killing the enemy
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

    private fun defend(): Int {

        if (actionPoints < hero.armor.actionPoints) {
            respond("You don't have enough action points to defend")
            return 0
        }
        hero.defend()
        actionPoints -= hero.armor.actionPoints

        respond("You now have $actionPoints action points")
        respond("You take a defensive position the ${enemy.name} now does ${hero.armor.absorption} less damgae")
        return 0
    }

    private fun useItem(input: String): Int {
        val selection = hero.inventory.filterIsInstance<Consumable>()

        try {
            if (selection[input.toInt()].description.split(" ")[0] == "heals") {
                hero.use(selection[input.toInt()], hero)
                respond("You healed yourself, your health is now ${hero.health}")
            } else {
                hero.use(selection[input.toInt()], enemy)
                respond("You damaged the enemy, the ${enemy.name}'s health is now ${enemy.health}")

                if (enemy.health <= 0) {
                    respond("You killed the ${enemy.name}")
                    // add coins for killing the enemy
                    val reward = 10 + Random.nextInt(-2, 2)
                    hero.coins += reward
                    respond("You recieved $reward coins")

                    mode = 0
                    return 1
                }
            }
        } catch (e: IndexOutOfBoundsException) {
            respond("no consumable with that index!")
        }
        return 0
    }

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

    private fun enemyTurn(): Int {
        if (enemy.stunned) {
            respond("the ${enemy.name} is stunned, it can't attack")
            return 0
        }
        if (hero.armor.name == "plate armor [2AP]") {
            damageMultiplierEnemy *= 0.8
        }
        enemy.attack(hero, damageMultiplierEnemy)

        damageMultiplierEnemy = 1.0

        // check if hero is dead
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

    private fun needlemanWunsch(string1: String, string2: String): Double {
        // match algorithm
        val rows = string1.length + 1
        val cols = string2.length + 1
        val matrix = Array(rows) { IntArray(cols) }
        var match: Int
        var left: Int
        var right: Int

        for (i in 0..<rows) {
            matrix[i][0] = i * -2
        }
        for (j in 0..<cols) {
            matrix[0][j] = j * -2
        }

        for (i in 1..<rows) {
            for (j in 1..<cols) {
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
        return matrix[rows - 1][cols - 1].toDouble() + 5.0
    }

    private fun respond(message: String?, bold: Boolean = true) {
        view.content.output.respond(message, bold)
        scrollToBottom()
    }

    private fun scrollToBottom() {
        view.content.scroll.verticalScrollBar.value = view.content.scroll.verticalScrollBar.maximum
    }
}
