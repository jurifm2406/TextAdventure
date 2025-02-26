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
        return true
    }

    private fun attack(): Boolean{
        hero.attack(enemy)
        if (enemy.health <= 0){
            view.content.output.respond("You killed the ${enemy.name}")
            // remove enemy
            hero.room.entities.remove(enemy)
            // enemy item drops
            return false
        }
        view.content.output.respond("You attacked ${enemy.name}, its health is now ${enemy.health}")

        return enemyTurn()
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

    private fun enemyTurn(): Boolean{

        enemy.attack(hero)

        // Check if hero is dead
        if (hero.health <= 0) {
            view.content.output.respond("You died!")
            for (item in hero.inventory) {
                hero.room.inventory.add(item)
            }
            hero.inventory.clear()
            view.content.output.respond("You respawned")
            return false

        }
        else {
            view.content.output.respond("A ${enemy.name} attacked, your health is now ${hero.health}")
            isCombatActive = true
            return true
        }
    }

}
