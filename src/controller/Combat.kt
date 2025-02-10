package controller

import model.objects.base.entities.Entity
import model.objects.base.entities.Hero
import view.View

class Combat(val enemy: Entity, val hero: Hero, val view: View) {
    var isCombatActive = true

    fun Combatparse(input:List<String>){
        if (isCombatActive) {
            when (input[0]) {
                "attack" -> {
                    view.content.output.respond(enemy.health.toString())
                    hero.attack(enemy)
                    view.content.output.respond("You attacked ${enemy.name}")
                    view.content.output.respond(enemy.health.toString())
                }
                "defend" -> defend()
                "use" -> useItem()
                "escape" -> escape()
                else -> view.content.output.respond("Invalid action.")
            }
        }
        else enemyTurn()

    }

    private fun defend() {
        isCombatActive = false
        // optional
    }

    private fun useItem() {
        isCombatActive = false
       // optional
    }

    private fun escape() {
        isCombatActive = false
        // optional
    }

    private fun enemyTurn() {


        enemy.attack(hero)
        // Check if hero is still alive
        if (hero.health <= 0) {
            // hero died
        }
        isCombatActive = true
    }

}
