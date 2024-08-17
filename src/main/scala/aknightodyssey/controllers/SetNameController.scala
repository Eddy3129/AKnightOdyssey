package aknightodyssey.controllers

import scalafx.event.ActionEvent
import scalafx.scene.control.{Button, TextField}
import scalafx.scene.text.Text
import scalafxml.core.macros.sfxml

@sfxml
class SetNameController (
                          private val textField: TextField,
                          private val submitButton: Button,
                          private val promptText: Text
                        ){
  var playerName: String = ""

  private def storeName(): Unit = {
    playerName = textField.text.value
    println(s"Player's name is: $playerName")
  }

  def handleStart(action: ActionEvent): Unit = {
    storeName()
    aknightodyssey.MainApp.showGameplay(playerName)
  }
}
