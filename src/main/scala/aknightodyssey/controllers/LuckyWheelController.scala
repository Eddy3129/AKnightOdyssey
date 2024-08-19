package aknightodyssey.controllers

import aknightodyssey.game.GameLogic
import scalafx.animation.{Interpolator, KeyFrame, RotateTransition, Timeline}
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.ImageView
import scalafx.util.Duration
import scalafxml.core.macros.sfxml

import scala.util.Random

@sfxml
class LuckyWheelController(
                            private val luckyWheel: ImageView,
                            private val spinButton: Button,
                            private val spinMessage: Label
                          ) {
  private var gameLogic: GameLogic = _
  private var result: String = _

  def setGameLogic(gl: GameLogic): Unit = {
    gameLogic = gl
  }

  def getResult: String = result

  def spinWheel(): Unit = {
    spinButton.disable = true
    val random = new Random()
    val finalAngle = random.nextInt(360)
    val totalRotations = 360 * 10 + finalAngle

    val rotateTransition = new RotateTransition {
      node = luckyWheel
      duration = Duration(4000)
      byAngle = totalRotations
      interpolator = Interpolator.EaseOut
      onFinished = _ => {
        result = determineResult(finalAngle)
        spinMessage.text = s"You landed on: $result"

        new Timeline {
          keyFrames = Seq(KeyFrame(Duration(2000), onFinished = _ => {
            spinButton.scene().getWindow.hide()
          }))
        }.play()
      }
    }

    rotateTransition.play()
  }

  private def determineResult(angle: Double): String = {
    // Normalize angle to 0-360 range
    val normalizedAngle = ((angle % 360) + 360) % 360

    // Shift angle to account for -22.5 to 22.5 as the first segment
    val adjustedAngle = (normalizedAngle + 22.5) % 360

    // Determine the segment based on the adjusted angle
    val segmentIndex = (adjustedAngle / 45).toInt

    val segments = List("Sword", "Mud", "Sword", "Mud", "Sword", "Mud", "Sword", "Mud")

    segments(segmentIndex)
  }
}
