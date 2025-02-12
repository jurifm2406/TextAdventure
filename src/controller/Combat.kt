package controller

import model.objects.base.entities.Entity
import model.objects.base.entities.Hero
import view.View

class Combat(val enemy: Entity, val hero: Hero, val view: View) {
    var isCombatActive = true

    fun combatParse(input:List<String>): Boolean{
        if (isCombatActive) {
            when (input[0]) {
                "attack" -> return(attack())
                "defend" -> defend()
                "use" -> useItem()
                "escape" -> escape()
                else -> view.content.output.respond("Invalid action.")
            }
        }
    }

    private fun attack(): Boolean{
        hero.attack(enemy)
        if (enemy.health <= 0){
            view.content.output.respond("You killed the ${enemy.name}")
            // get back to normal game
            // enemy item drops
            return false
        }
        view.content.output.respond("You attacked ${enemy.name}, its health is now ${enemy.health}")
        enemyTurn()
        return true
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

        // Check if hero is dead
        if (hero.health <= 0) {
            view.content.output.respond("You died!")
            // respawn
            return

        }
        else view.content.output.respond("A ${enemy.name} attacked, your health is now ${hero.health}")
        isCombatActive = true
    }

}
