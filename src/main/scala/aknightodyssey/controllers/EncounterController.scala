package aknightodyssey.controllers

import scalafx.scene.image.{Image, ImageView}
import scalafxml.core.macros.sfxml
import scalafx.scene.text.Text
import scalafx.animation._
import scalafx.util.Duration
import scalafx.Includes._

@sfxml
class EncounterController(
                           private val backgroundImage: ImageView,
                           private val encounterText: Text
                         ) {
  def initData(imagePath: String, text: String): Unit = {
    if (backgroundImage != null) {
      backgroundImage.image = new Image(imagePath)
      // Set the initial scale to zoomed in
      backgroundImage.scaleX = 1.2
      backgroundImage.scaleY = 1.2
    }
    if (encounterText != null) {
      encounterText.text = text
    }

    // Zoom out the image to original size
    val zoomOut = new ScaleTransition(Duration(2000), backgroundImage) {
      fromX = 1.2
      fromY = 1.2
      toX = 1.0
      toY = 1.0
    }

    // Fade out the image and close the window
    val fadeOut = new FadeTransition(Duration(2500), backgroundImage) {
      fromValue = 1.0
      toValue = 0.0
      onFinished = _ => backgroundImage.getScene.getWindow.hide()
    }

    // Sequentially play the zoom out and fade out animations
    val sequence = new SequentialTransition {
      children = Seq(zoomOut, fadeOut)
    }

    sequence.play()
  }
}
