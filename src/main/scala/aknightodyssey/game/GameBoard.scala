package aknightodyssey.game

import scala.util.Random
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.StackPane
import scalafx.scene.control.Label
import scalafx.scene.layout.GridPane

class GameBoard(val boardSize: Int = 30) {
  private val random: Random = new Random()

  val tiles: Array[Tile] = createBoard()

  //ensure each row got 2 or 3 special tiles
  private def createBoard(): Array[Tile] = {
    val boardSize = 30
    val board = new Array[Tile](boardSize + 1)
    board(1) = new NormalTile
    board(boardSize) = new NormalTile

    val rowSize = 6
    val numRows = 5

    for (row <- 0 until numRows) {
      val startIndex = row * rowSize + 2 // +2 to skip the first tile
      val endIndex = if (row == numRows - 1) boardSize - 1 else startIndex + rowSize // -1 to not include the last tile

      // Determine number of special tiles for this row (2 or 3)
      val numSpecialTiles = math.min(random.nextInt(3) + 2, endIndex - startIndex) // 2 or 3

      // Create a list of indices for this row and shuffle it
      val indices = random.shuffle((startIndex until endIndex).toList)

      // Place special tiles
      for (i <- 0 until numSpecialTiles) {
        val tileIndex = indices(i)
        board(tileIndex) = random.nextInt(3) match {
          case 0 => new PowerUpTile
          case 1 => new MonsterTile
          case 2 => new SpecialEncounterTile
        }
      }

      // Fill the rest with normal tiles
      for (i <- numSpecialTiles until indices.length) {
        val tileIndex = indices(i)
        board(tileIndex) = new NormalTile
      }
    }

    board
  }

  // Retrieve position of tile
  def getTileAt(position: Int): Tile = {
    if (position >= 1 && position <= boardSize) tiles(position) else new NormalTile
  }

  // Create UI for gameboard
  def createGameBoardUI(gameBoard: GridPane): Unit = {
    val tileSize: Int = 75
    val stoneTileImage = new Image("aknightodyssey/images/Gameplay-Tile-Block.png")
    val monsterIcon = new Image("aknightodyssey/images/Gameplay-Monster-Icon.jpeg")
    val powerUpIcon = new Image("aknightodyssey/images/Gameplay-Power-Up-Icon.jpeg")
    val specialEncounterIcon = new Image("aknightodyssey/images/Gameplay-Special-Icon.jpeg")
    val trophyIcon = new Image("aknightodyssey/images/Gameplay-Trophy-Icon.jpeg")

    for (i <- 0 until boardSize) {
      val tile = getTileAt(i + 1)

      val tileIcon = if (i == boardSize - 1) {
        //final tile is a trophy icon
        new ImageView(trophyIcon)
      } else {
        //different icon based on tile type
        tile match {
          case _: MonsterTile => new ImageView(monsterIcon)
          case _: PowerUpTile => new ImageView(powerUpIcon)
          case _: SpecialEncounterTile => new ImageView(specialEncounterIcon)
          case _ => null
        }
      }

      if (tileIcon != null) {
        tileIcon.fitWidth = tileSize
        tileIcon.fitHeight = tileSize
      }

      val tilePane = new StackPane {
        children = List(
          new ImageView(stoneTileImage) {
            fitWidth = tileSize
            fitHeight = tileSize
          },
          new Label((i + 1).toString) {
            style = "-fx-font-size: 40px; -fx-text-fill: white; -fx-font-weight: bold;"
          }
        ) ++ Option(tileIcon).toList
      }

      val row = 4 - (i / 6)
      val col = if (row % 2 == 0) i % 6 else 5 - (i % 6)

      gameBoard.add(tilePane, col, row)
    }
  }
}