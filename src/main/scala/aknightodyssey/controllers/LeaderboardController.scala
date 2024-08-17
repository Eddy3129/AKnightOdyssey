package aknightodyssey.controllers

import aknightodyssey.MainApp
import aknightodyssey.model.Leaderboard
import scalafx.scene.control.{TableColumn, TableView}
import scalafxml.core.macros.sfxml
import scalafx.Includes._
import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalafx.event.ActionEvent
import scalafx.scene.input.MouseEvent

@sfxml
class LeaderboardController(
                             private val leaderboardTable: TableView[(String, Int)],
                             private val playerNameColumn: TableColumn[(String, Int), String],
                             private val turnsColumn: TableColumn[(String, Int), Int]
                           ) {

  playerNameColumn.cellValueFactory = { cellData =>
    StringProperty(cellData.value._1)
  }

  turnsColumn.cellValueFactory = { cellData =>
    ObjectProperty(cellData.value._2)
  }

  leaderboardTable.items = Leaderboard.getScores

  def handleBack(event: MouseEvent): Unit = {
    MainApp.showMainMenu() // Navigate back to the main menu
  }
}