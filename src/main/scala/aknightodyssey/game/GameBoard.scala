package aknightodyssey.game

import aknightodyssey.game.Tile
import scala.util.Random

class GameBoard(val size: Int = 30) {
  private val random = new Random()

  // Initialize the board with random tiles
  val tiles: Array[Tile] = Array.fill(size) {
    random.nextInt(4) match {
      case 0 => new PowerUpTile
      case 1 => new MonsterTile
      case 2 => new SpecialEncounterTile
      case _ => new NormalTile
    }
  }

  def getTileAt(position: Int): Tile = {
    if (position >= 0 && position < size) tiles(position) else new NormalTile
  }
}
