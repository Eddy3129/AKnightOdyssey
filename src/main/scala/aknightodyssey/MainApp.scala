package aknightodyssey

import aknightodyssey.controllers.{GameOverOverlayController, GameplayController, SetNameController}
import aknightodyssey.util.Database
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import javafx.{scene => jfxs}
import scalafx.scene.layout.AnchorPane
import scalafx.stage.{Modality, Stage, StageStyle}

object MainApp extends JFXApp {
  private var mainStage: PrimaryStage = _

  Database.setupDB()  // Initialize the database

  stage = new PrimaryStage {
    title = "A Knight's Odyssey"
    maximized = true
  }
  mainStage = stage

  def showMainMenu(): Unit = {
    val resource = getClass.getResource("view/GameHomePage.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root = loader.getRoot[jfxs.layout.AnchorPane]
    mainStage.scene = new Scene(root)
  }

  def showGameplay(playerName: String): Unit = {
    val resource = getClass.getResource("view/Gameplay.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root = loader.getRoot[jfxs.Parent]
    val controller = loader.getController[GameplayController#Controller]
    controller.initializeGame(playerName) // Initialize the game
    mainStage.scene = new Scene(root)
  }

  def showSetName(): Unit = {
    val resource = getClass.getResource("view/SetName.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root = loader.getRoot[jfxs.Parent]
    mainStage.scene = new Scene(root)
  }

  def showLeaderboard(): Unit = {
    val resource = getClass.getResource("view/LeaderboardPage.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root = loader.getRoot[jfxs.Parent]
    mainStage.scene = new Scene(root)
  }

  def showGameOverlay(playerName: String, turnCount: Int): Unit = {
    val resource = getClass.getResource("view/GameOverOverlay.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()

    val root = loader.getRoot[jfxs.Parent]
    val controller = loader.getController[GameOverOverlayController#Controller]
    controller.setData(playerName, turnCount)

    val overlayStage = new Stage() {
      initModality(Modality.ApplicationModal)
      initStyle(StageStyle.Undecorated)
      scene = new Scene(root)
    }
    overlayStage.showAndWait()
  }

  showMainMenu()
}