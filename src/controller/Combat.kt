package controller

import model.objects.base.entities.Entity
import model.objects.base.entities.Hero
import view.View
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random
import kotlin.streams.asSequence


class Combat(private val enemy: Entity, private val hero: Hero, private val view: View) {
    private var damageMultiplierHero: Double = 1.0
    private var damageMultiplierEnemy: Double = 1.0
    private var mode = 0
    private var start: Long = 0
    private var end: Long = 0
    private var puzzle = ""
    private var score = 0.0
    private var actionPoints = 3

    fun combatParse(input: List<String>): Int {
        if (mode == 0) {
            when (input[0].lowercase()) {
                "attack" -> return (attack(input[0]))
                "defend" -> return (defend())
                "use" -> return (useItem())
                "escape" -> return (escape())
                "end" -> {
                    actionPoints += 3
                    enemy.tick()
                    hero.tick()
                    return enemyTurn()
                }

                else -> view.content.output.respond("Invalid action.")
            }
        } else if (mode == 1) {
            return attack(input[0])
        }
        return 0
    }

    private fun attack(input: String): Int {
        if (actionPoints < hero.weapon.actionPoints) {
            view.content.output.respond("You don't have enough actionpoints to use that weapon")
            return 0
        }
        if ((Random.nextInt(0, 10) < 4) || (mode == 1)) {
            if (mode == 0) {
                view.content.output.respond("You have the chance for critical damage")
                // generate puzzle
                val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz"
                puzzle = java.util.Random().ints(Random.nextInt(5, 10).toLong(), 0, source.length)
                    .asSequence()
                    .map(source::get)
                    .joinToString("")
                view.content.output.respond("Type this Sequence, as fast as possible")
                view.content.output.respond(puzzle)
                start = System.currentTimeMillis()
                // call view for
                mode = 1

                return 0

            } else if (mode == 1) {

                end = System.currentTimeMillis()
                score = (needlemanWunsch(puzzle, input) - ((end - start - puzzle.length * 300.0) / 1000.0)) / 10.0

                if (end - start > 2000 + puzzle.length * 750) {
                    actionPoints -= 3
                    view.content.output.respond("You were too slow, your attack is canceled")
                    mode = 0
                    return 0
                }
                val decimal = BigDecimal(score + 1).setScale(2, RoundingMode.HALF_EVEN)
                view.content.output.respond("Your score is $decimal")
                mode = 0

            }
        }
        if (mode == 0) {
            actionPoints -= hero.weapon.actionPoints
            view.content.output.respond("You now have $actionPoints action points")
            hero.attack(enemy, (damageMultiplierHero + score))

            damageMultiplierHero = 1.0
            if (enemy.health <= 0) {
                view.content.output.respond("You killed the ${enemy.name}")

                hero.room.entities.remove(enemy)

                // enemy item drops TODO
                mode = 0
                return 1
            }
            view.content.output.respond("You attacked ${enemy.name}, its health is now ${enemy.health}")
            mode = 0
            return 0
        }
        return 0
    }

    private fun defend(): Int {

        if (actionPoints < hero.armor.actionPoints) {
            view.content.output.respond("You don't have enough action points to defend")
            return 0
        }
        hero.defend()
        actionPoints -= hero.armor.actionPoints

        view.content.output.respond("You now have $actionPoints action points")
        view.content.output.respond("You take a defensive position the ${enemy.name} now does $damageMultiplierEnemy times damage")
        return 0
    }

    private fun useItem(): Int {
        // optional

        return 0
    }

    private fun escape(): Int {
        if (actionPoints < 3) {
            view.content.output.respond("You don't have enough action points to escape")
            return 0
        }
        actionPoints = 0
        if (Random.nextInt(0, 1) == 0) {
            view.content.output.respond("You escaped")
            return 3
        }
        view.content.output.respond("Your escape failed, the ${enemy.name} now does triple damage")
        damageMultiplierEnemy = 3.0

        return 0

    }

    private fun enemyTurn(): Int {

        if (enemy.stunned == true) {
            view.content.output.respond("The ${enemy.name} is stunned")
            return 0
        }
        enemy.attack(hero, damageMultiplierEnemy)

        damageMultiplierEnemy = 1.0

        // Check if hero is dead
        if (hero.health <= 0) {
            return 2
        } else {
            view.content.output.respond("A ${enemy.name} attacked, your health is now ${hero.health}")
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
}
