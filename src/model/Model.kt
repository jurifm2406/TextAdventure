package model

import javax.swing.text.PlainDocument

class Model {
    val outputModel = PlainDocument()
    val mapModel = MapModel(9)
    val map = Map(9)
}