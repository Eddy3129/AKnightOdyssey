package aknightodyssey.controllers

import scalafx.event.ActionEvent
import scalafxml.core.macros.sfxml

@sfxml
class GameHomePageController {
  def handleName(action: ActionEvent): Unit = {
    aknightodyssey.MainApp.showSetName()
  }
  def handleLeaderboard(action: ActionEvent): Unit = {
    aknightodyssey.MainApp.showLeaderboard()
  }
}