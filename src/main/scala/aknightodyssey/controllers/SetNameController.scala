package aknightodyssey.controllers

import aknightodyssey.game.Player
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
  private var player: Player = _
  private val MaxNameLength = 10

  def initialize(): Unit = {
    textField.text.onChange { (_, _, newValue) =>
      if (newValue.length > MaxNameLength) {
        textField.text = newValue.take(MaxNameLength)
      }
    }
  }

  //create player and restricting empty names and name exceeding 10 characters
  private def createPlayer(): Boolean = {
    val name = textField.text.value.trim
    if (name.isEmpty) {
      showAlert("Empty Name Not Allowed", "Please enter a valid name before starting the game.")
      false
    } else if (name.length > MaxNameLength) {
      showAlert("Name Too Long", s"Please enter a name with $MaxNameLength characters or less.")
      false
    } else {
      player = new Player(1, name)
      println(s"Player's name is: ${player.name}")
      true
    }
  }

  //display alerts for invalid input
  private def showAlert(header: String, content: String): Unit = {
    new Alert(AlertType.Warning) {
      initOwner(textField.scene().window())
      title = "Invalid Name"
      headerText = header
      contentText = content
    }.showAndWait()
  }

  //initiate gameplay after pressing button
  def handleStart(action: ActionEvent): Unit = {
    if (createPlayer()) {
      aknightodyssey.MainApp.showGameplay(player)
    }
  }
}