package aknightodyssey.controllers

import aknightodyssey.game.{GameBoard, GameLogic, MonsterTile, Player, SpecialEncounterTile}
import aknightodyssey.{MainApp, model}
import aknightodyssey.model.Leaderboard
import scalafx.scene.Scene
import scalafx.scene.control.{Alert, Button, Label}
import scalafx.scene.layout.{GridPane, StackPane}
import scalafxml.core.macros.sfxml
import scalafx.scene.image.{Image, ImageView}
import scalafx.stage.{Modality, Stage, StageStyle}
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import scalafx.scene.media.{AudioClip, Media, MediaPlayer}
import scalafx.Includes._
import scalafx.animation.{FadeTransition, KeyFrame, PauseTransition, Timeline}
import scalafx.application.Platform
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.shape.Rectangle
import scalafx.util.Duration

@sfxml
class GameplayController(
                          private val rollButton: Button,
                          private val messageLabel: Label,
                          private val messageContainer: Rectangle,
                          private val positionLabel: Label,
                          private val gameBoard: GridPane
                        ) {
  private var gameLogic: GameLogic = _
  private val boardSize = 30
  private val tileSize = 75
  private var playerToken: ImageView = _
  private var turnCount: Int = 0
  private var playerName: String = _

  def initializeGame(playerName: String): Unit = {
    this.playerName = playerName
    val gameBoardModel = new GameBoard()
    val player = new Player(1)
    gameLogic = new GameLogic(player, gameBoardModel)
    createGameBoard()
    createPlayerToken()
    updateUI()
  }

  def rollDice(): Unit = {
    val currentPosition = gameLogic.getCurrentPosition
    val (roll, message) = gameLogic.rollDiceAndMove()
    val newPosition = gameLogic.getCurrentPosition
    val movementMessage = s"Moving from $currentPosition to $newPosition."

      // After showing the roll, animate the tile-specific message
      animateRollMessage(s"Rolled $roll. $movementMessage\n$message"
    ) {
        // Once the message typing is complete, handle the encounter
        handleEncounter()

        // Update the UI only after the encounter is handled
        turnCount += 1
        updateUI()

        // Check for any debuffs
        if (gameLogic.player.isDebuffed) {
          Platform.runLater {
            openEncounterWindow(
              "/aknightodyssey/images/move_slowed.png",
              "You are stuck in mud! You can only roll up to 3.",
              "/aknightodyssey/sounds/mud_slowed.wav",
              triggerLuckyWheel = false
            )
          }
        }

        // Check if the game is over
        if (gameLogic.isGameOver) {
          endGame()
        }
      }
    }



  private def animateRollMessage(message: String)(onFinish: => Unit): Unit = {
    // Ensure the components are visible
    messageContainer.visible = true
    messageLabel.visible = true

    // Clear the label initially
    messageLabel.text = ""

    // Reset opacity for fade animations
    messageContainer.opacity = 1.0
    messageLabel.opacity = 1.0

    val typingDuration = 50
    val typingSound = new AudioClip(getClass.getResource("/aknightodyssey/sounds/digital_typing.wav").toString)

    // Timeline for typing effect
    val typingTimeline = new Timeline {
      keyFrames = (0 until message.length).map { i =>
        KeyFrame(Duration(typingDuration * i), onFinished = _ => {
          messageLabel.text.value += message.charAt(i)
          typingSound.play()
        })
      }
    }

    // Fade out animation for the rectangle after the message is typed
    val fadeOutTransition = new FadeTransition(Duration(3000), messageContainer) {
      fromValue = 1.0
      toValue = 0.5
    }

    // Chain animations
    typingTimeline.setOnFinished { _ =>
      fadeOutTransition.play()
      fadeOutTransition.setOnFinished(_ => onFinish)
    }

    // Start the typing animation
    typingTimeline.play()
  }



  private def handleEncounter(): Unit = {
    gameLogic.getCurrentTile match {
      case _: SpecialEncounterTile => handleSpecialEncounter()
      case _ => handleNormalEncounter()
    }
  }

  private def handleSpecialEncounter(): Unit = {
    gameLogic.getEncounterDetails.foreach { case (imagePath, musicPath, encounterText) =>
      Platform.runLater {
        openEncounterWindow(imagePath, encounterText, musicPath, triggerLuckyWheel = true)
      }
    }
  }

  private def handleNormalEncounter(): Unit = {
    gameLogic.getEncounterDetails.foreach { case (imagePath, musicPath, encounterText) =>
      Platform.runLater {
        openEncounterWindow(imagePath, encounterText, musicPath, triggerLuckyWheel = false)
      }
    }
  }

  def openEncounterWindow(imagePath: String, text: String, musicPath: String, triggerLuckyWheel: Boolean): Unit = {
    if (gameLogic.getCurrentTile.isInstanceOf[MonsterTile] && gameLogic.player.hasPowerBoost) {
      gameLogic.player.resetPowerBoost()
      openEncounterWindow("/aknightodyssey/images/Monster_Sword.png", "You have defeated the Goblin with the Mighty Gold Sword!", "/aknightodyssey/sounds/monster_sword.wav", triggerLuckyWheel = false)
      return
    }

    val resource = getClass.getResource("/aknightodyssey/view/Encounter.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root = loader.getRoot[javafx.scene.Parent]
    val controller = loader.getController[EncounterController#Controller]

    val stage = new Stage() {
      initModality(Modality.ApplicationModal)
      initStyle(StageStyle.Undecorated)
      scene = new Scene(root)
    }
    stage.setMaximized(true)

    val mediaResource = getClass.getResource(musicPath)
    if (mediaResource != null) {
      val media = new Media(mediaResource.toString)
      val mediaPlayer = new MediaPlayer(media)
      mediaPlayer.play()

      stage.onCloseRequest = _ => mediaPlayer.stop()
    }

    controller.initData(imagePath, text)
    stage.showAndWait()

    if (triggerLuckyWheel) {
      val luckyWheelResult = MainApp.showLuckyWheel(gameLogic)
      luckyWheelResult match {
        case "Sword" =>
          gameLogic.player.applyPowerBoost()
          messageLabel.text = "You received a Power Boost! You're immune to the next monster attack."
        case "Mud" =>
          gameLogic.player.applyDebuff()
          messageLabel.text = "Oh no! You're stuck in mud and can only roll up to 3 for the next 3 turns."
      }
    }
  }

  private def updateUI(): Unit = {
    positionLabel.text = s"Current Position: ${gameLogic.getCurrentPosition}"
    updatePlayerTokenPosition()
  }

  private def createGameBoard(): Unit = {
    val stoneTileImage = new Image("aknightodyssey/images/StoneTile.png")

    for (i <- 0 until boardSize) {
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
    if (position >= 0 && position < boardSize) {
      val row = 4 - (position / 6)
      val col = if (row % 2 == 0) position % 6 else 5 - (position % 6)

      playerToken.scaleX = if (row % 2 == 0) 1 else -1
      gameBoard.children.removeAll(playerToken)
      gameBoard.add(playerToken, col, row)
    } else {
      println(s"Invalid position: $position")
    }
  }


  private def endGame(): Unit = {
    messageLabel.text = s"Congratulations! You've reached the end of the game."
    rollButton.disable = true
    savePlayerScore()
    aknightodyssey.MainApp.showGameOverlay(playerName, turnCount)
  }

  private def savePlayerScore(): Unit = {
    Leaderboard.addScore(playerName, turnCount)
  }
}
