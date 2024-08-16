package aknightodyssey

import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import javafx.{scene => jfxs}

object MainApp extends JFXApp {
  stage = new PrimaryStage {
    title = "A Knight's Odyssey"
    maximized = true
  }

  def showMainMenu(): Unit = {
    val resource = getClass.getResource("view/GameHomePage.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root = loader.getRoot[jfxs.layout.AnchorPane]
    stage.scene = new Scene(root)
  }

  def showGameplay(): Unit = {
    val resource = getClass.getResource("view/Gameplay.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val root = loader.getRoot[jfxs.Parent]
    stage.scene = new Scene(root)
  }

  showMainMenu()
}