package controller

import model.objects.base.entities.Entity
import model.objects.base.entities.Hero
import view.View
import java.util.*
import kotlin.random.Random
import javax.swing.*;
import java.time.LocalTime
import kotlin.streams.asSequence
import java.math.BigDecimal
import java.math.RoundingMode


class Combat(val enemy: Entity, val hero: Hero, val view: View) {
    var damage_multiplier_hero: Double = 1.0
    var damage_multiplier_enemy: Double = 1.0
    var mode = 0
    var start = System.currentTimeMillis()
    var end = System.currentTimeMillis()
    var puzzle = ""
    var score = 0.0

    fun combatParse(input:List<String>): Int{
        if(mode == 0) {
            when (input[0]) {
                "attack" -> return (attack(input[0].lowercase()))
                "defend" -> return (defend())
                "use" -> return (useItem())
                "escape" -> return (escape())
                else -> view.content.output.respond("Invalid action.")
            }
        }
        else if(mode == 1) {
            return attack(input[0])
        }
        return 0
    }

    private fun attack(input:String): Int {
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

                mode = 1

                return 0

            } else if (mode == 1) {

                end = System.currentTimeMillis()
                score = (Needleman_Wunsch(puzzle, input) - ((end - start - puzzle.length * 300.0) / 1000.0)) / 10.0

                if (end - start > 2000 + puzzle.length * 750) {
                    view.content.output.respond("You were too slow, your attack is canceled")
                    mode = 0
                    return enemyTurn()
                }
                val decimal = BigDecimal(score + 1).setScale(2, RoundingMode.HALF_EVEN)
                view.content.output.respond("Your score is $decimal")
                mode = 0

            }
        }
        if (mode == 0) {
            hero.attack(enemy, (damage_multiplier_hero + score))

            damage_multiplier_hero = 1.0
            if (enemy.health <= 0) {
                view.content.output.respond("You killed the ${enemy.name}")
                // remove enemy

                hero.room.entities.remove(enemy)
                // enemy item drops
                mode = 0
                return 1
            }
            view.content.output.respond("You attacked ${enemy.name}, its health is now ${enemy.health}")
            mode = 0
            return enemyTurn()
        }
        return 0
    }

    private fun defend(): Int{
        // optional


        return 0
    }

    private fun useItem(): Int{
       // optional

        return 0
    }

    private fun escape(): Int{

        if (Random.nextInt(0, 1) == 0) {
            view.content.output.respond("You escaped")
            return 3
        }
        view.content.output.respond("Your escape failed, the ${enemy.name} now does triple damage")
        damage_multiplier_enemy = 3.0

        return enemyTurn()

    }

    private fun enemyTurn(): Int{

        enemy.attack(hero,damage_multiplier_enemy)

        damage_multiplier_enemy = 1.0

        // Check if hero is dead
        if (hero.health <= 0) {
            view.content.output.respond("You died!")
            for (item in hero.inventory) {
                hero.room.inventory.add(item)
            }
            hero.inventory.clear()
            view.content.output.respond("You respawned")
            return 2

        }
        else {
            view.content.output.respond("A ${enemy.name} attacked, your health is now ${hero.health}")
            return 0
        }
    }
    private fun Needleman_Wunsch(string1: String, string2: String): Double{
        // match algorithm
        val rows = string1.length + 1
        val cols = string2.length + 1
        val matrix = Array(rows) { IntArray(cols) }
        var match: Int
        var left: Int
        var right: Int

        for (i in 0 until rows) {
            matrix[i][0] = i * -2
        }
        for (j in 0 until cols) {
            matrix[0][j] = j * -2
        }

        for (i in 1 until rows) {
            for (j in 1 until cols) {
                if (string1[i - 1] == string2[j - 1]) {
                    match = matrix[i - 1][j - 1] + 1
                }
                else{
                    match = matrix[i - 1][j - 1] -1
                }
                left = matrix[i][j - 1] - 2
                right = matrix[i - 1][j] - 2
                matrix[i][j] = maxOf(left,right,match)
            }
        }
        return matrix[rows - 1][cols - 1].toDouble() + 5.0

    }

}
