package aknightodyssey.controllers

import scalafx.event.ActionEvent
import scalafxml.core.macros.sfxml

@sfxml
class GameHomePageController {
  def handleStart(action: ActionEvent): Unit = {
    aknightodyssey.MainApp.showGameplay()
  }
}