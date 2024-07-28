package aknightodyssey.controllers

import aknightodyssey.MainApp
import scalafx.event.ActionEvent
import scalafxml.core.macros.sfxml

@sfxml
class GameHomePageController(){

  def handleStart(action:ActionEvent): Unit = {

    MainApp.showGameplay()
  }

  def handleLeaderboard(action:ActionEvent): Unit = {

    MainApp.showLeaderboard()
  }


}

