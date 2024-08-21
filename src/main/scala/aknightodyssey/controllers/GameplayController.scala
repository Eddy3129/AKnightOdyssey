package aknightodyssey.controllers

import aknightodyssey.game.{GameBoard, GameLogic, MonsterTile, Player, PowerUpTile, SpecialEncounterTile}
import aknightodyssey.{MainApp}
import aknightodyssey.model.Leaderboard
import aknightodyssey.util.{EffectAnimation, MessageAnimation}
import javafx.fxml.FXML
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.{GridPane, StackPane}
import scalafx.scene.image.{Image, ImageView}
import scalafx.stage.{Modality, Stage, StageStyle}
import scalafxml.core.macros.sfxml
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import scalafx.scene.media.{Media, MediaPlayer}
import scalafx.scene.shape.Rectangle
import scalafx.util.Duration
import scalafx.animation.{FadeTransition, KeyFrame, PauseTransition, Timeline}
import scalafx.Includes._

@sfxml
class GameplayController(
                          @FXML private val rollButton: Button,
                          @FXML private val messageLabel: Label,
                          @FXML private val effectLabel: Label,
                          @FXML private val messageContainer: Rectangle,
                          @FXML private val positionLabel: Label,
                          @FXML private val gameBoard: GridPane
                        ) {
  private var gameLogic: GameLogic = _
  private val boardSize:Int = 30
  private val tileSize:Int = 75
  private var playerToken: ImageView = _
  private var turnCount: Int = 0
  private var playerName: String = _
  private val messageAnimation: MessageAnimation = new MessageAnimation(messageLabel, 50)
  private val effectAnimation: EffectAnimation = new EffectAnimation(effectLabel, 50)
  private var luckyWheelSpunThisTurn: Boolean = false

  def initializeGame(playerName: String): Unit = {
    this.playerName = playerName
    gameLogic = new GameLogic(new Player(1), new GameBoard())
    createGameBoard()
    createPlayerToken()
    updatePlayerTokenPosition()
  }

  def rollDice(): Unit = {
    turnCount += 1
    luckyWheelSpunThisTurn = false
    rollButton.disable = true
    val (roll, oldPosition, tileMessage, encounterDetails) = gameLogic.processMove()
    val movementMessage = s"Rolled $roll. Moving from $oldPosition to ${gameLogic.getCurrentPosition}."

    messageAnimation.play(List(movementMessage, tileMessage)) {
      updatePlayerTokenPosition()

      gameLogic.getCurrentTile match {
        case _: MonsterTile =>
          handleMonsterEncounter(encounterDetails)

        case _: SpecialEncounterTile =>
          encounterDetails.foreach { case (imagePath, musicPath, encounterText) =>
            openEncounterWindow(imagePath, encounterText, musicPath, true, () => {
              applyTileEffectsAndCheckGameOver()
            })
          }

        case _ =>
          encounterDetails match {
            case Some((imagePath, musicPath, encounterText)) =>
              openEncounterWindow(imagePath, encounterText, musicPath, false, () => {
                applyTileEffectsAndCheckGameOver()
              })
            case None =>
              applyTileEffectsAndCheckGameOver()
          }
      }
    }
  }


  private def handleMonsterEncounter(encounterDetails: Option[(String, String, String)]): Unit = {
    // Check if the player has a power boost (sword)
    val hasPowerBoost = gameLogic.player.hasPowerBoost

    // Determine the correct image, sound, and text based on whether the player has a power boost
    val (imagePath, musicPath, encounterText) = if (hasPowerBoost) {
      ("/aknightodyssey/images/Gameplay-Monster-Encounter-PlayerBoost.png",
        "/aknightodyssey/sounds/Gameplay_Monster-Encounter-PlayerBoost.wav",
        "You have defeated the Goblin with the Mighty Gold Sword!")
    } else {
      encounterDetails.getOrElse(("/aknightodyssey/images/Gameplay-Monster-Encounter.jpg",
        "/aknightodyssey/sounds/Gameplay-Monster-Encounter.wav",
        "You have encountered the Goblin Monster"))
    }

    // Handle the monster encounter logic
    val monsterEffect = gameLogic.handleMonsterEncounter()

    // Open the encounter window with the correct image, sound, and text
    openEncounterWindow(imagePath, encounterText, musicPath, false, () => {
      effectAnimation.play(List(monsterEffect)) {
        updatePlayerTokenPosition()
        rollButton.disable = false
      }
    })
  }

  private def applyTileEffectsAndCheckGameOver(): Unit = {
    val effects = gameLogic.applyTileEffects()

    if (effects.nonEmpty) {
      effectAnimation.play(effects) {
        updatePlayerTokenPosition()
        if (gameLogic.isGameOver) {
          endGame()
        } else {
          rollButton.disable = false
        }
      }
    } else {
      updatePlayerTokenPosition()
      if (gameLogic.isGameOver) {
        endGame()
      } else {
        rollButton.disable = false
      }
    }
  }

  def openEncounterWindow(imagePath: String, text: String, musicPath: String, triggerLuckyWheel: Boolean, onClose: () => Unit, isVictoryScreen: Boolean = false): Unit = {
    val resource = getClass.getResource("/aknightodyssey/view/Encounter.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root = loader.getRoot[javafx.scene.Parent]
    val controller = loader.getController[EncounterController#Controller]

    val stage = new Stage() {
      initModality(Modality.ApplicationModal)
      initStyle(StageStyle.Unified)
      scene = new Scene(root)
    }
    stage.setMaximized(true)

    val mediaResource = getClass.getResource(musicPath)
    if (mediaResource != null) {
      val media = new Media(mediaResource.toString)
      val mediaPlayer = new MediaPlayer(media)
      mediaPlayer.play()

      if (isVictoryScreen) {
        // For victory screen, keep the window open longer
        val delay = new PauseTransition(Duration(5000))
        delay.onFinished = _ => {
          mediaPlayer.stop()
          stage.close()
          onClose()
        }
        delay.play()
      } else {
        stage.onHidden = _ => {
          mediaPlayer.stop()

          if (triggerLuckyWheel) {
            showLuckyWheel(onClose)
          } else {
            onClose()
          }
        }
      }
    }

    controller.initData(imagePath, text)
    stage.show()
  }

  private def showLuckyWheel(onClose: () => Unit): Unit = {
    if (!luckyWheelSpunThisTurn) {
      luckyWheelSpunThisTurn = true
      rollButton.disable = true // Ensure the roll button is disabled
      MainApp.showLuckyWheel(gameLogic, result => {
        println(s"Lucky Wheel result: $result")

        val effectMessage = result match {
          case "Sword" =>
            gameLogic.player.applyPowerBoost()
            "You gained immunity against the next monster attack!"
          case "Mud" =>
            gameLogic.player.applyDebuff()
            "You are stuck in mud! You can only roll up to 3 for the next 3 turns."
          case _ => "Unknown effect"
        }

        // Use PauseTransition to add a slight delay before showing the effect message
        new PauseTransition(Duration(500)) {
          onFinished = _ => {
            effectAnimation.play(List(effectMessage)) {
              rollButton.disable = false // Enable the button after effects
              onClose()
            }
          }
        }.play()
      })
    } else {
      println("Lucky Wheel already spun this turn")
      rollButton.disable = false // Enable the button if Lucky Wheel is not shown
      onClose()
    }
  }


  private def createGameBoard(): Unit = {
    val stoneTileImage = new Image("aknightodyssey/images/Gameplay-Tile-Block.png")
    val monsterIcon = new Image("aknightodyssey/images/Gameplay-Monster-Icon.jpeg")
    val powerUpIcon = new Image("aknightodyssey/images/Gameplay-Power-Up-Icon.jpeg")
    val specialEncounterIcon = new Image("aknightodyssey/images/Gameplay-Special-Icon.jpeg")
    val trophyIcon = new Image("aknightodyssey/images/Gameplay-Trophy-Icon.jpeg")  // Add this line

    for (i <- 0 until boardSize) {
      val tile = gameLogic.gameBoard.getTileAt(i + 1)

      val tileIcon = if (i == boardSize - 1) {
        new ImageView(trophyIcon)  // Use trophy icon for the last tile
      } else {
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

  private def createPlayerToken(): Unit = {
    playerToken = new ImageView(new Image("aknightodyssey/images/Gameplay-Player-Token.png")) {
      fitWidth = 150
      fitHeight = 150
    }
    updatePlayerTokenPosition()
  }

  private def updatePlayerTokenPosition(): Unit = {
    positionLabel.text = s"Current Position: ${gameLogic.getCurrentPosition}"
    println(s"Updating player token position to ${gameLogic.getCurrentPosition}")
    val position = Math.min(gameLogic.getCurrentPosition - 1, boardSize - 1)
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
    rollButton.disable = true
    savePlayerScore()

    openEncounterWindow(
      "/aknightodyssey/images/Gameplay-Victory-Endscreen.png",
      s"Congratulations, $playerName! You've completed your journey in $turnCount turns.",
      "/aknightodyssey/sounds/Gameplay-Endscreen-BGM.wav",
      false,
      () => {
        Platform.runLater {
          aknightodyssey.MainApp.showGameOverlay(playerName, turnCount)
        }
      },
      isVictoryScreen = true
    )
  }

  private def savePlayerScore(): Unit = {
    Leaderboard.addScore(playerName, turnCount)
  }
}
