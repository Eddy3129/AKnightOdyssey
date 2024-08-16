package aknightodyssey.controllers

import aknightodyssey.game.GameLogic
import scalafx.scene.control.{Button, Label}
import scalafxml.core.macros.sfxml

@sfxml
class GameplayController(
                          private val rollButton: Button,
                          private val messageLabel: Label,
                          private val positionLabel: Label
                        ) {
  private var gameLogic: GameLogic = _

  def initializeGame(): Unit = {
    gameLogic = new GameLogic()
    updateUI()
  }

  def rollDice(): Unit = {
    val (roll, message) = gameLogic.rollDiceAndMove()
    messageLabel.text = s"Rolled $roll. $message"
    updateUI()

    if (gameLogic.isGameOver) {
      rollButton.disable = true
      messageLabel.text = "Congratulations! You've reached the end of the game."
    }
  }

  private def updateUI(): Unit = {
    positionLabel.text = s"Current Position: ${gameLogic.getCurrentPosition}"
  }

}



