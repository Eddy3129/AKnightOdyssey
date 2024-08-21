package aknightodyssey.controllers

import javafx.fxml.FXML
import scalafx.event.ActionEvent
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Button, TextField}
import scalafx.scene.text.Text
import scalafxml.core.macros.sfxml
import scalafx.Includes._

@sfxml
class SetNameController (
                          @FXML private val textField: TextField,
                          @FXML private val submitButton: Button,
                          @FXML private val promptText: Text
                        ){
  var playerName: String = ""

  private def storeName(): Boolean = {
    val name = textField.text.value.trim
    if (name.isEmpty) {
      showEmptyNameAlert()
      false
    } else {
      playerName = name
      println(s"Player's name is: $playerName")
      true
    }
  }

  private def showEmptyNameAlert(): Unit = {
    new Alert(AlertType.Warning) {
      initOwner(textField.scene().window())
      title = "Invalid Name"
      headerText = "Empty Name Not Allowed"
      contentText = "Please enter a valid name before starting the game."
    }.showAndWait()
  }

  def handleStart(action: ActionEvent): Unit = {
    if (storeName()) {
      aknightodyssey.MainApp.showGameplay(playerName)
    }
  }
}
