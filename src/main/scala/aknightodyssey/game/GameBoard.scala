package aknightodyssey.game

import scala.util.Random

class GameBoard(val size: Int = 30) {
  private val random: Random = new Random()

  val tiles: Array[Tile] = createBoard()

  private def createBoard(): Array[Tile] = {
    val board = new Array[Tile](size + 1)

    board(1) = new NormalTile
    board(size) = new NormalTile

    var specialEncounterCount = 0
    var powerUpCount = 0
    var monsterCount = 0

    // Fill the rest of the board
    for (i <- 2 until size) {
      var tileCreated = false
      while (!tileCreated) {
        random.nextInt(4) match {
          case 0 if powerUpCount < 4 =>
            board(i) = new PowerUpTile
            powerUpCount += 1
            tileCreated = true
          case 1 if monsterCount < 4 =>
            board(i) = new MonsterTile
            monsterCount += 1
            tileCreated = true
          case 2 if specialEncounterCount < 3 =>
            board(i) = new SpecialEncounterTile
            specialEncounterCount += 1
            tileCreated = true
          case _ if (size - i) > (7 - specialEncounterCount - powerUpCount - monsterCount) =>
            board(i) = new NormalTile
            tileCreated = true
        }
      }
    }

    board
  }

  def getTileAt(position: Int): Tile = {
    if (position >= 1 && position <= size) tiles(position) else new NormalTile
  }
}