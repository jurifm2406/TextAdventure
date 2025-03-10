package model

import model.objects.base.entities.Enemy
import model.objects.base.entities.Entity
import model.objects.base.item.Armor
import model.objects.base.item.Consumable
import model.objects.base.item.Weapon
import kotlin.random.Random

/**
 * the object that holds templates for all weapons, armors, consumables and enemies to be changed and used during map creation
 */
object Data {
    val weapons: Array<Weapon> = arrayOf(
        Weapon("hammer [3AP]", "stuns", 45, 3, effects = mutableListOf({ e: Entity ->
            if (Random.nextInt(2) == 0) {
                e.stunned = true
            }
        })),
        Weapon("poisoned dagger [1AP]", "poisons", 15, 1, effects = mutableListOf({ it.damage(5) })),
        Weapon("longsword [2AP]", "enhances crits", 30, 2),
        Weapon("whip [1AP]", "bleed", 15, 1, effects = mutableListOf({ if (Random.nextInt(4) == 0) it.damage(30) }))
    )

    val armors: Array<Armor> = arrayOf(
        Armor("vampire's robes [1AP]", "life steal", 7, 1),
        Armor("cloak [1AP]", "enhances crit chance", 7, 1),
        Armor("paladin's chest plate [2AP]", "protects from death once", 12, 2),
        Armor("plate armor [2AP]", "all enemies deal 20% less damage", 12, 2)
    )

    val consumables: Array<Consumable> = arrayOf(
        Consumable("small health potion", "heals 15 health", { entity -> entity.heal(15) }),
        Consumable("medium health potion", "heals 25 health", { entity -> entity.heal(25) }),
        Consumable("large health potion", "heals 40 health", { entity -> entity.heal(40) }),
        Consumable("throwing dagger", "deals 10 damage", { entity -> entity.damage(10) })
    )

    val enemies: Array<Enemy> = arrayOf(
        Enemy("Orc", 150),
        Enemy("Thief", 120),
        Enemy("Goblin", 100),
        Enemy("Troll", 180),
        Enemy("Bandit", 120),
        Enemy("Skeleton", 90),
        Enemy("Dark Mage", 130),
        Enemy("Giant Spider", 110),
        Enemy("Werewolf", 160),
        Enemy("Demon", 180)
    )
}