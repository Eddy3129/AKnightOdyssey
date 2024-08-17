package aknightodyssey.game

import scala.util.Random

class GameBoard {
  val ROWS = 6
  val COLUMNS = 5
  val TOTAL_TILES: Int = ROWS * COLUMNS

  val player = new Player(0, this)

  val gameBoard: Array[Tiles] = Array.fill(TOTAL_TILES) {
    Random.nextInt(7) match {
      case 0 => new PowerUpTile
      case 1 => new MonsterTile
      case 2 => new SpecialEncounterTile
      case _ => new NormalTile
    }
  }

  def rollDiceAndMove(): (Int, String) = {
    val roll = Random.nextInt(6) + 1
    player.move(roll)
    (roll, gameBoard(player.position).encounter(player))
  }

  // New method to get the current tile
  def getCurrentTile: Tiles = gameBoard(player.position)
}
