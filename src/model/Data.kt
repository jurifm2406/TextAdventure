package model

import model.objects.base.entities.Enemy
import model.objects.base.item.Armor
import model.objects.base.item.Consumable
import model.objects.base.item.Weapon

object Data {
    val weapons: Array<Weapon> = arrayOf(
        Weapon("hammer", "stuns", 0),
        Weapon("poisoned dagger", "poisons", 0),
        Weapon("longsword", "enhances crits", 0),
        Weapon("whip", "bleed", 0)
    )

    val armors: Array<Armor> = arrayOf(
        Armor("vampire's robes", "life steal", 0),
        Armor("cloak", "enhances crit chance", 0)
    )

    val consumables: Array<Consumable> = arrayOf(
        Consumable("small health potion", "heals 10 health", { entity -> entity.heal(10) }),
        Consumable("medium health potion", "heals 25 health", { entity -> entity.heal(25) }),
        Consumable("large health potion", "heals 40 health", { entity -> entity.heal(40) }),
        Consumable("throwing dagger", "deals 10 damage", { entity -> entity.damage(10) })
    )

    val enemies: Array<Enemy> = arrayOf(
        Enemy("Orc", 150),
        Enemy("Thief", 100),
        Enemy("Goblin", 80),
        Enemy("Troll", 200),
        Enemy("Bandit", 120),
        Enemy("Skeleton", 90),
        Enemy("Dark Mage", 130),
        Enemy("Giant Spider", 110),
        Enemy("Werewolf", 180),
        Enemy("Demon", 250)
    )
}