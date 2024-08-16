package aknightodyssey.controllers

import scalafx.scene.image.{Image, ImageView}
import scalafxml.core.macros.sfxml
import scalafx.scene.text.Text
import scalafx.animation.PauseTransition
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
    }
    if (encounterText != null) {
      encounterText.text = text
    }

    // Create a PauseTransition for 3 seconds
    val delay = new PauseTransition(Duration(3000))
    delay.onFinished = _ => {
      // Close the window
      Option(backgroundImage.scene()).foreach(_.window().hide())
    }
    delay.play()
  }
}