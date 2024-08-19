package aknightodyssey

import aknightodyssey.controllers.{GameOverOverlayController, GameplayController, LuckyWheelController, SetNameController}
import aknightodyssey.game.GameLogic
import aknightodyssey.util.Database
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import scalafx.Includes._
import scalafx.application.{JFXApp, Platform}
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
    Platform.runLater {
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
  }


  // In MainApp object
  def showLuckyWheel(gameLogic: GameLogic): String = {
    val resource = getClass.getResource("/aknightodyssey/view/LuckyWheel.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root = loader.getRoot[javafx.scene.Parent]
    val controller = loader.getController[LuckyWheelController#Controller]

    controller.setGameLogic(gameLogic)

    val stage = new Stage() {
      initModality(Modality.ApplicationModal)
      initStyle(StageStyle.Undecorated)
      scene = new Scene(root)
    }
    stage.showAndWait()

    controller.getResult
  }



  showMainMenu()
}