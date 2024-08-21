package aknightodyssey.controllers

import aknightodyssey.MainApp
import aknightodyssey.model.Leaderboard
import javafx.fxml.FXML
import scalafx.scene.control.{Alert, TableColumn, TableView}
import scalafx.scene.control.Alert.AlertType
import scalafx.event.ActionEvent
import scalafx.Includes._
import scalafx.scene.input.MouseEvent
import scalafxml.core.macros.sfxml

import scala.util.{Failure, Success}

@sfxml
class LeaderboardController(
                             @FXML private val leaderboardTable: TableView[(String, Int)],
                             @FXML private val playerNameColumn: TableColumn[(String, Int), String],
                             @FXML private val turnsColumn: TableColumn[(String, Int), Int]
                           ) {

  playerNameColumn.cellValueFactory = { cellData =>
    scalafx.beans.property.StringProperty(cellData.value._1)
  }

  turnsColumn.cellValueFactory = { cellData =>
    scalafx.beans.property.ObjectProperty(cellData.value._2)
  }

  def refreshLeaderboard(): Unit = {
    leaderboardTable.items = Leaderboard.getScores
  }

  def handleBack(event: MouseEvent): Unit = {
    MainApp.showMainMenu()
  }

  def handleDelete(action: ActionEvent): Unit = {
    val selectedIndex = leaderboardTable.selectionModel().selectedIndex.value
    if (selectedIndex >= 0) {
      val (playerName, turnCount) = leaderboardTable.items.value(selectedIndex)
      Leaderboard.deleteScore(playerName, turnCount) match {
        case Success(_) => refreshLeaderboard()
        case Failure(e) =>
          new Alert(AlertType.Error) {
            initOwner(leaderboardTable.scene().window())
            title = "Delete Error"
            headerText = "Failed to delete score"
            contentText = s"An error occurred: ${e.getMessage}"
          }.showAndWait()
      }
    } else {
      new Alert(AlertType.Information) {
        initOwner(leaderboardTable.scene().window())
        title = "No Selection"
        headerText = "No Score Selected"
        contentText = "Please select a score in the table."
      }.showAndWait()
    }
  }

  // Call this method when initializing the controller
  refreshLeaderboard()
}