package aknightodyssey.controllers

import javafx.fxml.FXML
import scalafx.event.ActionEvent
import scalafx.scene.control.{Button, Label}
import scalafxml.core.macros.sfxml

/**
 * This controller is responsible for handling the game over overlay after game ends
 */

@sfxml
class GameOverOverlayController(
                                 @FXML private val congratsLabel: Label,
                                 @FXML private val turnsLabel: Label,
                                 @FXML private val showLeaderboardButton: Button,
                                 @FXML private val exitButton: Button
                               ) {

  /*Display player name and score upon game ends*/
  def setData(playerName: String, turnCount: Int): Unit = {
    congratsLabel.text = s"Congratulations, $playerName!"
    turnsLabel.text = s"You won the game in $turnCount turns!"
  }

  /*Player can navigate to leaderboard*/
  def handleShowLeaderboard(action: ActionEvent): Unit = {
    congratsLabel.getScene.getWindow.hide()
    aknightodyssey.MainApp.showLeaderboard()
  }

  /*Player can exit to main menu*/
  def handleExit(action: ActionEvent): Unit = {
    congratsLabel.getScene.getWindow.hide()
    aknightodyssey.MainApp.showMainMenu()
  }
}
