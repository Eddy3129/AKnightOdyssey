package aknightodyssey.game

import scala.util.Random

class GameLogic(val player: Player, val gameBoard: GameBoard) {
  private val random = new Random()

  def rollDiceAndMove(): (Int, Int) = {
    val maxRoll = if (player.isDebuffed) 3 else 6
    val roll = random.nextInt(maxRoll) + 1
    val oldPosition = player.position
    player.move(roll)
    player.decrementDebuff()
    ensureValidPosition()
    (roll, oldPosition)
  }

  def getCurrentPosition: Int = player.position

  def isGameOver: Boolean = getCurrentPosition >= gameBoard.size

  def getEncounterDetails: Option[(String, String, String)] = {
    val details = getCurrentTile.getEncounterDetails
    details
  }

  def getCurrentTile: Tile = {
    val tile = gameBoard.getTileAt(player.position)
    tile
  }

  def processMove(): (Int, Int, String, Option[(String, String, String)]) = {
    val (roll, oldPosition) = rollDiceAndMove()
    val positionAfterRoll = player.position
    val tileType = getCurrentTileType
    val message = s"Landed on ${tileType} tile at $positionAfterRoll."

    val encounterDetails = getEncounterDetails
    (roll, oldPosition, message, encounterDetails)
  }

  def handleMonsterEncounter(): String = {
    if (player.hasPowerBoost) {
      player.resetPowerBoost()
      "You defeated the monster with your Mighty Gold Sword!"
    } else {
      val oldPosition = player.position
      player.position = Math.max(1, player.position - 5)
      val steps = oldPosition - player.position
      s"Moving back $steps steps from $oldPosition to ${player.position}."
    }
  }

  def applyTileEffects(): List[String] = {
    val effects = List.newBuilder[String]
    val currentTile = getCurrentTile

    currentTile match {
      case _: MonsterTile =>
      // Monster effects are handled separately in handleMonsterEncounter()

      case _: PowerUpTile =>
        val oldPosition = player.position
        player.position = Math.min(oldPosition + 3, gameBoard.size)
        effects += s"Moving forward 3 steps from $oldPosition to ${player.position}."

      case _: SpecialEncounterTile =>
      // Handle special encounter logic if necessary

      case _ =>
      // Handle normal tile or any other tile types
    }

    ensureValidPosition()
    effects.result()
  }


  private def ensureValidPosition(): Unit = {
    if (player.position > gameBoard.size) {
      player.position = gameBoard.size
    }
  }

  private def getCurrentTileType: String = {
    getCurrentTile match {
      case _: MonsterTile => "Monster"
      case _: PowerUpTile => "Power-Up"
      case _: SpecialEncounterTile => "Special Encounter"
      case _ => "Normal"
    }
  }
}
