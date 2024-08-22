package aknightodyssey.controllers

import aknightodyssey.game.{GameBoard, GameLogic, MonsterTile, Player, PowerUpTile, SpecialEncounterTile}
import aknightodyssey.MainApp
import aknightodyssey.model.Leaderboard
import aknightodyssey.util.{EffectAnimation, MessageAnimation}
import javafx.fxml.FXML
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.GridPane
import scalafx.stage.{Modality, Stage, StageStyle}
import scalafxml.core.macros.sfxml
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import scalafx.scene.media.{Media, MediaPlayer}
import scalafx.scene.shape.Rectangle
import scalafx.util.Duration
import scalafx.animation.{FadeTransition, KeyFrame, PauseTransition, Timeline}
import scalafx.Includes._

/**
 * This controller is responsible for handling the gameplay fxml, which includes user interactions and UI updates during gameplay, and logic handling when game ends
 */

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
  private var gameboard: GameBoard = _
  private val boardSize: Int = 30
  private var player: Player = _
  private var turnCount: Int = 0
  private val messageAnimation: MessageAnimation = new MessageAnimation(messageLabel, 50)
  private val effectAnimation: EffectAnimation = new EffectAnimation(effectLabel, 50)
  private var luckyWheelSpunThisTurn: Boolean = false

  /*Initialize the game with the player object*/
  def initializeGame(player: Player): Unit = {
    this.player = player
    this.gameboard = new GameBoard()
    this.gameLogic = new GameLogic(player, gameboard)
    gameboard.createGameBoardUI(gameBoard)
    player.createPlayerToken()
    updatePlayerTokenPosition()
  }

  /*Roll dice button handling*/
  def rollDice(): Unit = {
    turnCount += 1
    luckyWheelSpunThisTurn = false
    rollButton.disable = true
    val (roll, oldPosition, tileMessage, encounterDetails) = gameLogic.processMove()
    val movementMessage = s"Rolled $roll. Moving from $oldPosition to ${gameLogic.getCurrentPosition}."

    messageAnimation.play(List(movementMessage, tileMessage)) {
      updateUIState()
      handleTileEncounter(encounterDetails)
    }
  }

  private def handleTileEncounter(encounterDetails: Option[(String, String, String)]): Unit = {
    gameLogic.getPlayerTile match {
      case _: MonsterTile => handleMonsterEncounter(encounterDetails)
      case _: SpecialEncounterTile =>
        encounterDetails.foreach { case (imagePath, musicPath, encounterText) =>
          showEncounterAndContinue(imagePath, encounterText, musicPath, triggerLuckyWheel = true)
        }
      case _ =>
        encounterDetails match {
          case Some((imagePath, musicPath, encounterText)) =>
            showEncounterAndContinue(imagePath, encounterText, musicPath)
          case None => applyTileEffectsAndCheckGameOver()
        }
    }
  }

  /*explicitly handles monster encounter scenario*/
  private def handleMonsterEncounter(encounterDetails: Option[(String, String, String)]): Unit = {
    val hasPowerBoost = gameLogic.player.hasSwordImmunity
    val (imagePath, musicPath, encounterText) = if (hasPowerBoost) {
      ("/aknightodyssey/images/Gameplay-Monster-Encounter-PlayerBoost.png",
        "/aknightodyssey/sounds/Gameplay_Monster-Encounter-PlayerBoost.wav",
        "You have defeated the Goblin with the Mighty Gold Sword!")
    } else {
      encounterDetails.getOrElse(("/aknightodyssey/images/Gameplay-Monster-Encounter.jpg",
        "/aknightodyssey/sounds/Gameplay-Monster-Encounter.wav",
        "You have encountered the Goblin Monster"))
    }

    val monsterEffect = gameLogic.handleMonsterEncounterLogic()
    showEncounter(imagePath, encounterText, musicPath, false, () => {
      effectAnimation.play(List(monsterEffect)) {
        applyTileEffectsAndCheckGameOver()
      }
    })
  }

  private def showEncounterAndContinue(imagePath: String, text: String, musicPath: String,
                                       triggerLuckyWheel: Boolean = false,
                                       onClose: () => Unit = () => applyTileEffectsAndCheckGameOver()): Unit = {
    showEncounter(imagePath, text, musicPath, triggerLuckyWheel, onClose)
  }

  /*After every move, call this function to apply tile effects and check if game is over*/
  private def applyTileEffectsAndCheckGameOver(): Unit = {
    val effects = gameLogic.applyTileEffects()
    def continueGame(): Unit = {
      updateUIState()
      if (gameLogic.isGameOver) endGame() else rollButton.disable = false
    }

    if (effects.nonEmpty) effectAnimation.play(effects)(continueGame())
    else continueGame()
  }

  private def updateUIState(): Unit = {
    positionLabel.text = s"Current Position: ${gameLogic.getCurrentPosition}"
    player.updatePlayerTokenPosition(gameBoard, boardSize)
  }


  /**
   * Opens an encounter window with the specified details.
   *
   * @param imagePath Image path for the encounter
   * @param text Text to display in the encounter window
   * @param musicPath Path to the music file for the encounter
   * @param triggerLuckyWheel Whether to trigger the lucky wheel after the encounter
   * @param onClose Callback function to execute when the encounter window is closed
   * @param isVictoryScreen Whether this is the victory screen
   */
  def showEncounter(imagePath: String, text: String, musicPath: String, triggerLuckyWheel: Boolean, onClose: () => Unit, isVictoryScreen: Boolean = false): Unit = {
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

    controller.initialize(imagePath, text)
    stage.show()
  }

  /*Method to display lucky wheel*/
  private def showLuckyWheel(onClose: () => Unit): Unit = {
    if (!luckyWheelSpunThisTurn) {
      luckyWheelSpunThisTurn = true
      rollButton.disable = true
      MainApp.showLuckyWheel(gameLogic, result => {
        val effectMessage = result match {
          case "Sword" =>
            gameLogic.player.applySwordImmunity()
            "You gained immunity against the next monster attack!"
          case "Mud" =>
            gameLogic.player.applyMud()
            "You are stuck in mud! You can only roll up to 3 for the next 3 turns."
          case _ => "Unknown effect"
        }

        new PauseTransition(Duration(500)) {
          onFinished = _ => {
            effectAnimation.play(List(effectMessage)) {
              rollButton.disable = false
              onClose()
            }
          }
        }.play()
      })
    } else {
      println("Lucky Wheel already spun this turn")
      rollButton.disable = false
      onClose()
    }
  }

  /*Updates player token position*/
  private def updatePlayerTokenPosition(): Unit = {
    positionLabel.text = s"Current Position: ${gameLogic.getCurrentPosition}"
    player.updatePlayerTokenPosition(gameBoard, boardSize)
  }

  /*End game handling mechanisms*/
  private def endGame(): Unit = {
    rollButton.disable = true
    savePlayerScore()

    showEncounter(
      "/aknightodyssey/images/Gameplay-Victory-Endscreen.png",
      s"Congratulations, ${player.name}! You've completed your journey in $turnCount turns.",
      "/aknightodyssey/sounds/Gameplay-Endscreen-BGM.wav",
      false,
      () => {
        Platform.runLater {
          aknightodyssey.MainApp.showGameOverlay(player.name, turnCount)
        }
      },
      isVictoryScreen = true
    )
  }

  /*Save player score to leaderboard*/
  private def savePlayerScore(): Unit = {
    Leaderboard.addScore(player.name, turnCount)
  }
}
