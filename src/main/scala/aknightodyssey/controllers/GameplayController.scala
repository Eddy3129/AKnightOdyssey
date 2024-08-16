package aknightodyssey.controllers

import aknightodyssey.game.{GameLogic, MonsterTile, NormalTile, PowerUpTile, SpecialEncounterTile, Tiles}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.{GridPane, StackPane}
import scalafxml.core.macros.sfxml
import scalafx.scene.image.{Image, ImageView}
import scalafx.stage.{Modality, Stage}
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import scalafx.Includes._


@sfxml
class GameplayController(
                          private val rollButton: Button,
                          private val messageLabel: Label,
                          private val positionLabel: Label,
                          private val gameBoard: GridPane

                        ) {
  private var gameLogic: GameLogic = _
  private val boardSize = 30
  private val tileSize = 75
  private var playerToken: ImageView = _

  def initializeGame(): Unit = {
    gameLogic = new GameLogic()
    createGameBoard()
    createPlayerToken()
    updateUI()
  }

  def rollDice(): Unit = {
    val (roll, message) = gameLogic.rollDiceAndMove()
    messageLabel.text = s"Rolled $roll. $message"
    updateUI()

    val encounterText = message match {
      case msg if msg.contains("monster") => "You encountered a monster!"
      case msg if msg.contains("power-up") => "You found a power-up!"
      case msg if msg.contains("special encounter") => "You've experienced a special encounter!"
      case _ => "Something happened!"
    }

    if (message.contains("monster") || message.contains("power-up") || message.contains("special encounter")) {
      openEncounterWindow("aknightodyssey/images/knight_run.jpeg", encounterText)
    }

    if (gameLogic.isGameOver) {
      rollButton.disable = true
      messageLabel.text = "Congratulations! You've reached the end of the game."
    }
  }

  private def updateUI(): Unit = {
    positionLabel.text = s"Current Position: ${gameLogic.getCurrentPosition}"
    updatePlayerTokenPosition()
  }

  private def createGameBoard(): Unit = {
    val stoneTileImage = new Image("aknightodyssey/images/StoneTile.png")

    for (i <- 0 until boardSize) {
      val tile = createTile(i + 1)
      val imageView = new ImageView(stoneTileImage) {
        fitWidth = tileSize
        fitHeight = tileSize
      }
      val label = new Label((i + 1).toString) {
        style = "-fx-font-size: 40px; -fx-text-fill: white; -fx-font-weight: bold;"
      }

      val tilePane = new StackPane {
        children = List(imageView, label)
      }

      // Calculate row and column for snake-like pattern
      val row = 4 - (i / 6)
      val col = if (row % 2 == 0) i % 6 else 5 - (i % 6)

      gameBoard.add(tilePane, col, row)
    }
  }

  private def createPlayerToken(): Unit = {
    val playerImage = new Image("aknightodyssey/images/Knight.png")
    playerToken = new ImageView(playerImage) {
      fitWidth = 150
      fitHeight = 150
    }
    updatePlayerTokenPosition()
  }

  private def updatePlayerTokenPosition(): Unit = {
    val position = gameLogic.getCurrentPosition - 1
    val row = 4 - (position / 6)
    val col = if (row % 2 == 0) position % 6 else 5 - (position % 6)

    playerToken.scaleX = if (row % 2 == 0) 1 else -1
    gameBoard.children.removeAll(playerToken)
    gameBoard.add(playerToken, col, row)
  }

  private def createTile(number: Int): Tiles = {
    number match {
      case n if n % 10 == 0 => new MonsterTile
      case n if n % 7 == 0 => new PowerUpTile
      case n if n % 5 == 0 => new SpecialEncounterTile
      case _ => new NormalTile
    }
  }

  private def openEncounterWindow(imagePath: String, text: String): Unit = {
    val resource = getClass.getResource("/aknightodyssey/view/Encounter.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root = loader.getRoot[javafx.scene.Parent]
    val controller = loader.getController[EncounterController#Controller]

    val stage = new Stage() {
      initModality(Modality.ApplicationModal)
      scene = new Scene(root)
    }

    controller.initData(imagePath, text)
    stage.show()
  }

}
