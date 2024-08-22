package aknightodyssey.game

import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.GridPane

class Player(var position: Int, val name: String) {
  private var stuckInMudTurns: Int = 0
  private var swordImmunity: Boolean = false
  private var playerToken: ImageView = _

  def move(steps: Int): Unit = {
    position += steps
  }

  def isStuckInMud: Boolean = stuckInMudTurns > 0

  def applyMud(): Unit = {
    stuckInMudTurns = 3
  }

  def decrementStuckInMudTurns(): Unit = {
    if (stuckInMudTurns > 0) stuckInMudTurns -= 1
  }

  def applySwordImmunity(): Unit = {
    swordImmunity = true
  }

  def resetSwordImmunity(): Unit = {
    swordImmunity = false
  }

  def hasSwordImmunity: Boolean = swordImmunity

  def createPlayerToken(): Unit = {
    playerToken = new ImageView(new Image("aknightodyssey/images/Gameplay-Player-Token.png")) {
      fitWidth = 150
      fitHeight = 150
    }
  }

  def updatePlayerTokenPosition(gameBoard: GridPane, boardSize: Int): Unit = {
    if (position >= 1 && position <= boardSize) {
      val pos = position - 1
      val row = 4 - (pos / 6)
      val col = if (row % 2 == 0) pos % 6 else 5 - (pos % 6)

      playerToken.scaleX = if (row % 2 == 0) 1 else -1
      gameBoard.children.removeAll(playerToken)
      gameBoard.add(playerToken, col, row)
    }
  }

}