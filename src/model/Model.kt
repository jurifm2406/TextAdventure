package model

import model.objects.base.entities.Hero
import javax.swing.text.PlainDocument

fun PlainDocument.addText(text: String) {
    this.insertString(
        this.length,
        text + "\n",
        null
    )
}

class Model(heroName: String) {
    val outputModel = PlainDocument()
    val mapModel = MapModel(9)
    val map = Map(9)
    val hero = Hero(heroName, map.startRoom)
}