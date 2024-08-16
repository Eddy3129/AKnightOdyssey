package aknightodyssey.game

class GameLogic {
  val gameBoard = new GameBoard()
  val player = gameBoard.player

  def rollDiceAndMove(): (Int, String) = {
    gameBoard.rollDiceAndMove()
  }

  def isGameOver: Boolean = {
    player.position == gameBoard.TOTAL_TILES - 1
  }

  def getCurrentPosition: Int = {
    player.position + 1
  }
}