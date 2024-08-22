package aknightodyssey.game

import scala.util.Random

class GameLogic(val player: Player, val gameBoard: GameBoard) {
  private val random = new Random()

  def processMove(): (Int, Int, String, Option[(String, String, String)]) = {
    val (roll, oldPosition) = rollDiceAndMove()
    val tileType = getCurrentTileType
    val message = s"Landed on ${tileType} tile at ${player.position}."
    val encounterDetails = getPlayerTile.getEncounterDetails
    (roll, oldPosition, message, encounterDetails)
  }

  def handleMonsterEncounterLogic(): String = {
    if (player.hasSwordImmunity) {
      player.resetSwordImmunity()
      "You defeated the monster with your Mighty Gold Sword!"
    } else {
      movePlayerBack(5)
    }
  }
  def applyTileEffects(): List[String] = {
    val effects = List.newBuilder[String]
    getPlayerTile match {
      case _: PowerUpTile =>
        effects += movePlayerForward(3)
      case _ => // No effect for other tile types
    }
    effects.result()
  }

  def isGameOver: Boolean = player.position >= gameBoard.boardSize

  def getCurrentPosition: Int = player.position

  def getPlayerTile: Tile = gameBoard.getTileAt(player.position)

  private def rollDiceAndMove(): (Int, Int) = {
    val maxRoll = if (player.isStuckInMud) 3 else 6
    val roll = random.nextInt(maxRoll) + 1
    val oldPosition = player.position
    player.move(roll)
    player.decrementStuckInMudTurns()
    ensureValidPosition()
    (roll, oldPosition)
  }

  private def movePlayerBack(steps: Int): String = {
    val oldPosition = player.position
    player.position = Math.max(1, player.position - steps)
    ensureValidPosition()
    s"Moving back ${oldPosition - player.position} steps from $oldPosition to ${player.position}."
  }

  private def movePlayerForward(steps: Int): String = {
    val oldPosition = player.position
    player.position = Math.min(oldPosition + steps, gameBoard.boardSize)
    ensureValidPosition()
    s"Moving forward $steps steps from $oldPosition to ${player.position}."
  }

  private def ensureValidPosition(): Unit = {
    if (player.position > gameBoard.boardSize) {
      player.position = gameBoard.boardSize
    }
  }

  private def getCurrentTileType: String = {
    getPlayerTile match {
      case _: MonsterTile => "Monster"
      case _: PowerUpTile => "Power-Up"
      case _: SpecialEncounterTile => "Special Encounter"
      case _ => "Normal"
    }
  }
}