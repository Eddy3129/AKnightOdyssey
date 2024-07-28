package aknightodyssey.game

class Player(var position: Int = 0, gameBoard: GameBoard) {
  def move(steps: Int): Unit = {
    position += steps
    if (position >= gameBoard.TOTAL_TILES) position = gameBoard.TOTAL_TILES - 1
  }
}
