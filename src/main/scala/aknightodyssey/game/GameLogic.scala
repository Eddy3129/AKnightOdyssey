package aknightodyssey.game

import scala.util.Random

class GameLogic(val player: Player, val gameBoard: GameBoard) {
  private val random = new Random()

  //Process effects after every player movement
  def processMove(): (Int, Int, String, Option[(String, String, String)]) = {
    val (roll, oldPosition) = rollDiceAndMove()
    println(s"Player position after move: ${player.position}")
    val playerTile = getPlayerTile
    val tileType = getCurrentTileType
    val message = s"Landed on ${tileType} tile at ${player.position}."
    val encounterDetails = if (playerTile != null) playerTile.getEncounterDetails else None
    (roll, oldPosition, message, encounterDetails)
  }

  //return string for special monster encounter cases
  def handleMonsterEncounterLogic(): String = {
    if (player.hasSwordImmunity) {
      player.resetSwordImmunity()
      "You defeated the monster with your Mighty Gold Sword!"
    } else {
      movePlayerBack(5)
    }
  }

  //return effect after landing on special tiles
  def applyTileEffects(): List[String] = {
    val effects = List.newBuilder[String]
    getPlayerTile match {
      case _: PowerUpTile =>
        effects += movePlayerForward(3)
      case _ => // No effect for other tile types
    }
    effects.result()
  }

  //check for game over if player position is equal or greater to game board size
  def isGameOver: Boolean = player.position >= gameBoard.boardSize

  //get current position of player
  def getCurrentPosition: Int = player.position

  //get current tile of player's position
  def getPlayerTile: Tile = gameBoard.getTileAt(player.position)

  //roll dice logic, treated differently when stuck in mud debuff is active
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

  //validate player position to never exceed gameboard range
  private def ensureValidPosition(): Unit = {
    if (player.position > gameBoard.boardSize) {
      player.position = gameBoard.boardSize
    }
  }

  //return String of current title type for effect message display
  private def getCurrentTileType: String = {
    getPlayerTile match {
      case _: MonsterTile => "Monster"
      case _: PowerUpTile => "Power-Up"
      case _: SpecialEncounterTile => "Special Encounter"
      case _ => "Normal"
    }
  }
}