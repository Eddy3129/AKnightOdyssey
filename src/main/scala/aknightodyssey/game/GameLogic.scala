package aknightodyssey.game

import scala.util.Random

class GameLogic(val player: Player, val gameBoard: GameBoard) {
  private val random = new Random()

  def rollDiceAndMove(): (Int, String) = {
    val roll = if (player.isDebuffed) random.nextInt(3) + 1 else random.nextInt(6) + 1
    player.move(roll)
    player.decrementDebuff()
    ensureValidPosition()
    (roll, getCurrentTileMessage)
  }

  def getCurrentPosition: Int = player.position

  def isGameOver: Boolean = player.position == gameBoard.size - 1

  def getEncounterDetails: Option[(String, String, String)] = {
    getCurrentTile.getEncounterDetails
  }

  def getCurrentTile: Tile = {
    gameBoard.getTileAt(player.position)
  }

  private def ensureValidPosition(): Unit = {
    if (player.position >= gameBoard.size) {
      player.position = gameBoard.size - 1
    }
  }

  private def getCurrentTileMessage: String = {
    getCurrentTile.getMessage
  }
}
