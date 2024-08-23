package aknightodyssey

import aknightodyssey.controllers.{GameOverOverlayController, GameplayController, LuckyWheelController, SetNameController}
import aknightodyssey.game.{GameBoard, GameLogic, Player}
import aknightodyssey.util.Database
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import scalafx.Includes._
import scalafx.application.{JFXApp, Platform}
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import javafx.{scene => jfxs}
import scalafx.stage.{Modality, Stage, StageStyle}


object MainApp extends JFXApp {
  private var mainStage: PrimaryStage = _

  Database.setupDB()  // Initialize the database

  stage = new PrimaryStage {
    title = "A Knight's Odyssey"
    maximized = true
  }
  mainStage = stage

    //Display game homepage
  def showMainMenu(): Unit = {
    val resource = getClass.getResource("view/GameHomePage.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root = loader.getRoot[jfxs.layout.AnchorPane]
    mainStage.scene = new Scene(root)
  }

  //Display gameplay interface
  def showGameplay(player: Player): Unit = {
    val resource = getClass.getResource("/aknightodyssey/view/Gameplay.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root = loader.getRoot[jfxs.Parent]
    val controller = loader.getController[GameplayController#Controller]

    stage.scene = new Scene(root)
    controller.initializeGame(player)
  }

  //Display Set Name page
  def showSetName(): Unit = {
    val resource = getClass.getResource("view/SetName.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root = loader.getRoot[jfxs.Parent]
    mainStage.scene = new Scene(root)
  }

  //Display Leaderboard
  def showLeaderboard(): Unit = {
    val resource = getClass.getResource("view/Leaderboard.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root = loader.getRoot[jfxs.Parent]
    mainStage.scene = new Scene(root)
  }

  //Display Game Over Overlay after game end
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

  //Display Lucky Wheel
  def showLuckyWheel(gameLogic: GameLogic, onResult: String => Unit): Unit = {
    val resource = getClass.getResource("/aknightodyssey/view/LuckyWheel.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root = loader.getRoot[javafx.scene.Parent]
    val controller = loader.getController[LuckyWheelController#Controller]
    controller.setGameLogic(gameLogic)
    controller.setResultCallback { result: String =>
      onResult(result)
    }
    val stage = new Stage() {
      initModality(Modality.ApplicationModal)
      initOwner(mainStage)
      initStyle(StageStyle.Undecorated)
      scene = new Scene(root)
    }
    stage.show()
  }

  //Show main menu when starting the game
  showMainMenu()
}