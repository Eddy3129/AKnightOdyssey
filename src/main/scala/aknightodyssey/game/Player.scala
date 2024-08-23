package aknightodyssey.game

import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.GridPane

class Player(var position: Int, val name: String) {
  private var stuckInMudTurns: Int = 0
  private var swordImmunity: Boolean = false
  private var playerToken: ImageView = _

  //move player
  def move(steps: Int): Unit = {
    position += steps
  }

  def isStuckInMud: Boolean = stuckInMudTurns > 0

  //Apply stuck in mud debuff for 3 turns
  def applyMud(): Unit = {
    stuckInMudTurns = 3
  }

  //Decrement mud debuff after every turn
  def decrementStuckInMudTurns(): Unit = {
    if (stuckInMudTurns > 0) stuckInMudTurns -= 1
  }

  //Apply sword immunity to player
  def applySwordImmunity(): Unit = {
    swordImmunity = true
  }

  def resetSwordImmunity(): Unit = {
    swordImmunity = false
  }

  def hasSwordImmunity: Boolean = swordImmunity

  //Create image of player token
  def createPlayerToken(): Unit = {
    playerToken = new ImageView(new Image("aknightodyssey/images/Gameplay-Player-Token.png")) {
      fitWidth = 150
      fitHeight = 150
    }
  }

  //Update position of player token and flip sides for even numbered roles
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