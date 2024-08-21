package aknightodyssey.controllers

import javafx.fxml.FXML
import scalafx.event.ActionEvent
import scalafx.scene.control.{Button, Label}
import scalafxml.core.macros.sfxml

@sfxml
class GameOverOverlayController(
                                 @FXML private val congratsLabel: Label,
                                 @FXML private val turnsLabel: Label,
                                 @FXML private val showLeaderboardButton: Button,
                                 @FXML private val exitButton: Button
                               ) {

  private var playerName: String = _
  private var turnCount: Int = _

  def setData(playerName: String, turnCount: Int): Unit = {
    congratsLabel.text = s"Congratulations, $playerName!"
    turnsLabel.text = s"You won the game in $turnCount turns!"
  }

  def handleShowLeaderboard(action: ActionEvent): Unit = {
    congratsLabel.getScene.getWindow.hide()
    aknightodyssey.MainApp.showLeaderboard()
  }

  def handleExit(action: ActionEvent): Unit = {
    congratsLabel.getScene.getWindow.hide()
    aknightodyssey.MainApp.showMainMenu()
  }
}
