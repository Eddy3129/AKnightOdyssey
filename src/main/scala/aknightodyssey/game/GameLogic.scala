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

  def getEncounterDetails: Option[(String, String, String)] = {
    val tile = gameBoard.getCurrentTile
    tile match {
      case _: MonsterTile => Some(("/aknightodyssey/images/Monster.jpg", "/aknightodyssey/sounds/monster_scream.wav", tile.encounter(player)))
      case _: PowerUpTile => Some(("/aknightodyssey/images/Power-up.jpg", "/aknightodyssey/sounds/horse_neigh.mp3", tile.encounter(player)))
      case _: SpecialEncounterTile => Some(("/aknightodyssey/images/Special-Encounter.jpg", "/aknightodyssey/sounds/special.mp3", tile.encounter(player)))
      case _ => None
    }
  }

}
