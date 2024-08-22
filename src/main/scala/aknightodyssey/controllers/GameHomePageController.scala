package aknightodyssey.controllers

import scalafx.event.ActionEvent
import scalafxml.core.macros.sfxml

/**
 * This controller is responsible for handling the game homepage fxml, which includes opening the setName page or leaderboard.
 */

@sfxml
class GameHomePageController {
  /*Open SetName fxml*/
  def handleName(action: ActionEvent): Unit = {
    aknightodyssey.MainApp.showSetName()
  }
  /*Open Leaderboard fxml*/
  def handleLeaderboard(action: ActionEvent): Unit = {
    aknightodyssey.MainApp.showLeaderboard()
  }
}