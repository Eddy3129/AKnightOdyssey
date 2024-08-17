package aknightodyssey.controllers

import scalafx.event.ActionEvent
import scalafx.scene.control.{Button, Label}
import scalafxml.core.macros.sfxml

@sfxml
class GameOverOverlayController(
                                 private val congratsLabel: Label,
                                 private val turnsLabel: Label,
                                 private val showLeaderboardButton: Button,
                                 private val exitButton: Button
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
