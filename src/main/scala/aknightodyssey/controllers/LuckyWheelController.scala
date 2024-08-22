package aknightodyssey.controllers

import aknightodyssey.game.GameLogic
import javafx.fxml.FXML
import scalafx.animation.{Interpolator, KeyFrame, RotateTransition, Timeline}
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.ImageView
import scalafx.util.Duration
import scalafxml.core.macros.sfxml

import scala.util.Random
import scalafx.application.Platform

@sfxml
class LuckyWheelController(
                            @FXML private val luckyWheel: ImageView,
                            @FXML private val spinButton: Button,
                            @FXML private val spinMessage: Label
                          ) {
  private var gameLogic: GameLogic = _
  private var resultCallback: String => Unit = _
  private val random = new Random()
  private val segments = List("Sword", "Mud", "Sword", "Mud", "Sword", "Mud", "Sword", "Mud")

  def initialize(): Unit = {
    spinButton.onAction = _ => spinWheel()
  }

  def setGameLogic(gl: GameLogic): Unit = {
    gameLogic = gl
  }

  def setResultCallback(callback: String => Unit): Unit = {
    resultCallback = callback
  }

  def spinWheel(): Unit = {
    spinButton.disable = true
    spinMessage.text = "Spinning..."
    val finalAngle = random.nextInt(360)
    val totalRotations = 360 * 10 + finalAngle

    new RotateTransition {
      node = luckyWheel
      duration = Duration(4000)
      byAngle = totalRotations
      interpolator = Interpolator.EaseOut
      onFinished = _ => handleSpinResult(finalAngle)
    }.play()
  }

  private def handleSpinResult(finalAngle: Double): Unit = {
    val result = determineResult(finalAngle)
    spinMessage.text = s"You landed on: $result"

    new Timeline {
      keyFrames = Seq(KeyFrame(Duration(1000), onFinished = _ => {
        Platform.runLater {
          resultCallback(result)
          closeWindow()
        }
      }))
    }.play()
  }

  private def determineResult(angle: Double): String = {
    val normalizedAngle = ((angle % 360) + 360) % 360
    val adjustedAngle = (normalizedAngle + 22.5) % 360
    val segmentIndex = (adjustedAngle / 45).toInt
    segments(segmentIndex)
  }

  private def closeWindow(): Unit = {
    Option(spinButton.scene().getWindow).foreach(_.hide())
  }
}