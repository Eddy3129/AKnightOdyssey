package aknightodyssey.controllers

import aknightodyssey.game.GameLogic
import scalafx.animation.{Interpolator, KeyFrame, RotateTransition, Timeline}
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.ImageView
import scalafx.util.Duration
import scalafxml.core.macros.sfxml
import scala.util.Random
import scalafx.application.Platform

@sfxml
class LuckyWheelController(
                            private val luckyWheel: ImageView,
                            private val spinButton: Button,
                            private val spinMessage: Label
                          ) {
  private var gameLogic: GameLogic = _
  private var resultCallback: String => Unit = _
  private var spinCompleted: Boolean = false

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
    if (!spinCompleted) {
      spinButton.disable = true
      spinMessage.text = "Spinning..."
      val random = new Random()
      val finalAngle = random.nextInt(360)
      val totalRotations = 360 * 10 + finalAngle
      val rotateTransition = new RotateTransition {
        node = luckyWheel
        duration = Duration(4000)
        byAngle = totalRotations
        interpolator = Interpolator.EaseOut
        onFinished = _ => {
          val result = determineResult(finalAngle)
          spinMessage.text = s"You landed on: $result"
          spinCompleted = true
          applyResultAndClose(result)
        }
      }
      rotateTransition.play()
    }
  }

  private def determineResult(angle: Double): String = {
    val normalizedAngle = ((angle % 360) + 360) % 360
    val adjustedAngle = (normalizedAngle + 22.5) % 360
    val segmentIndex = (adjustedAngle / 45).toInt
    val segments = List("Sword", "Mud", "Sword", "Mud", "Sword", "Mud", "Sword", "Mud")
    segments(segmentIndex)
  }

  private def applyResultAndClose(result: String): Unit = {
    new Timeline {
      keyFrames = Seq(KeyFrame(Duration(2000), onFinished = _ => {
        Platform.runLater {
          if (resultCallback != null) {
            resultCallback(result)
          }
          closeWindow()
        }
      }))
    }.play()
  }

  private def closeWindow(): Unit = {
    Option(spinButton.scene().getWindow).foreach(_.hide())
  }
}