package model

import model.objects.base.Inventory
import model.objects.base.entities.Enemy
import model.objects.base.item.Armor
import model.objects.base.item.Weapon

object Data {

    val weapons: Array<Weapon> = arrayOf(Weapon("Dagger", "A small, lightweight blade for quick and stealthy strikes.", 20),
        Weapon("Shortsword", "A versatile, one-handed sword ideal for close combat.", 40),
        Weapon("Longsword", "A balanced sword with reach for precise strikes.", 50),
        Weapon("Rapier", "A slender, agile weapon for thrusting attacks.", 45),
        Weapon("Warhammer", "A heavy, two-handed hammer for smashing through armor.", 70),
        Weapon("Dual-wielded-daggers", "Two lightweight blades for rapid, dual-weapon attacks.", 35),
        Weapon("Katana", "A sharp, precision sword with a chance to land critical hits.", 55),
        Weapon("Whip", "A long, flexible weapon for disarming and tripping foes.", 55),
        Weapon("Staff", "A versatile polearm for both melee and ranged attacks.", 40),
        Weapon("Flintlock-pistol", "A single-shot firearm with high damage but slow reload.", 80),
        Weapon("Crossbow", "A ranged weapon with a mechanism for shooting bolts.", 55),
        Weapon("Battleaxe", "A double-headed axe, excellent for both cutting and smashing.", 65),
        Weapon("Spear", "A long weapon with a sharp point for thrusting attacks.", 30),
        Weapon("Mace", "A blunt weapon with a heavy head for powerful strikes.", 50),
        Weapon("Greatsword", "A massive, two-handed sword for devastating swings.", 75),
        Weapon("Throwing-knives", "Small, lightweight blades designed for throwing.", 25),
        Weapon("War-scythe", "A curved blade mounted on a pole, ideal for sweeping attacks.", 60),
        Weapon("Bo-staff", "A long staff used for striking and blocking in martial arts.", 35),
        Weapon("Blunderbuss", "A short-barreled firearm with a wide muzzle for close-range shots.", 70),
        Weapon("Claymore", "A large, two-handed sword with a distinctive hilt and guard.", 80)
    )

    val armors: Array<Armor> = arrayOf( Armor("Chainmail", "Interlinked metal rings providing decent defense.", 2, 40.0),
        Armor("Studded-leather", "Leather reinforced with metal studs for added durability.", 1, 35.0),
        Armor("Scale-mail", "Armor made of overlapping metal scales.", 2, 45.0),
        Armor("Plate-armor", "Heavy and imposing armor for maximum protection", 4, 70.0),
        Armor("Barbarian-fur-armor", "Thick fur armor for protection against the cold and physical attacks.", 2, 50.0),
        Armor("Paladin's-holy-plate", "Armor imbued with holy magic for protection against evil.", 3, 60.0),
        Armor("Sorcerer's-enchanted-robe", "Robes enchanted with magical barriers.", 3, 40.0),
        Armor("Knight's-shining-armor", "Shiny, highly protective armor for noble knights.", 4, 80.0),
        Armor("Dwarven-ironclad", "Heavy armor crafted by dwarves for maximum protection.", 4, 75.0),
        Armor("Assassin's-Shadow-Cloak", "A cloak that grants enhanced stealth and agility to assassins.", 1, 30.0),
        Armor("Elven-Leafweave", "Armor made from magically imbued leaves, providing both protection and agility.", 3, 35.0),
        Armor("Dragonbone-Plate", "Armor forged from the bones of a dragon, offering unparalleled defense.", 4, 90.0),
        Armor("Mystic-Silk-Robes", "Robes woven from mystical silk, granting protection and enhancing magical abilities.", 4, 40.0),
        Armor("Necromancer's-Shroud", "A shroud imbued with necrotic energies, providing dark protection.", 4, 55.0)
    )

    val enemies: Array<Enemy> = arrayOf(
        Enemy("Orc", 150),
        Enemy("Thief", 100),
        Enemy("Goblin", 80),
        Enemy("Troll", 200),
        Enemy("Bandit", 120),
        Enemy("Skeleton Warrior", 90),
        Enemy("Dark Mage", 130),
        Enemy("Giant Spider", 110),
        Enemy("Werewolf", 180),
        Enemy("Demon", 250)
    )
}