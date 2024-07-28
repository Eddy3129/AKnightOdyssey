package aknightodyssey

import aknightodyssey.game.{GameBoard, Player}

object MainApp extends App {
  // Initialize the game
  val gameBoard = new GameBoard()
  val player = gameBoard.player

  // Function to display the board
  def displayBoard(): Unit = {
    val ROWS = gameBoard.ROWS
    val COLUMNS = gameBoard.COLUMNS
    val TOTAL_TILES = gameBoard.TOTAL_TILES
    for (row <- 0 until ROWS) {
      for (col <- 0 until COLUMNS) {
        val index = if (row % 2 == 0) row * COLUMNS + col + 1 else row * COLUMNS + (COLUMNS - 1 - col) + 1
        if (player.position == index-1) {
          print("[P] ")
        } else {
          print(s"[$index] ")
        }
      }
      println()
    }
    println()
  }

  // Game loop
  var continuePlaying = true
  while (continuePlaying) {
    displayBoard()
    println("Press Enter to roll the dice...")
    scala.io.StdIn.readLine()
    val (roll, message) = gameBoard.rollDiceAndMove()
    println(s"Rolled $roll. $message Player is now on tile ${player.position + 1}")
    if (player.position == gameBoard.TOTAL_TILES - 1) {
      println("Congratulations! You've reached the end of the game.")
      continuePlaying = false
    }
  }
}

