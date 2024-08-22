package aknightodyssey.model

import aknightodyssey.util.Database
import scalafx.collections.ObservableBuffer
import scalikejdbc._

import scala.util.{Failure, Success, Try}

object Leaderboard extends Database {
  val scores: ObservableBuffer[(String, Int)] = ObservableBuffer.empty[(String, Int)]

  def initializeTable(): Unit = {
    DB autoCommit { implicit session =>
      sql"""
        create table Leaderboard (
          id int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
          playerName varchar(64),
          turnCount int
        )
      """.execute.apply()
    }
  }

  def addScore(playerName: String, turnCount: Int): Unit = {
    DB autoCommit { implicit session =>
      sql"""
        INSERT INTO Leaderboard (playerName, turnCount)
        VALUES (${playerName}, ${turnCount})
      """.update.apply()
    }
    loadScoresFromDB()
  }

  def getScores: ObservableBuffer[(String, Int)] = {
    loadScoresFromDB()
    scores
  }

  private def loadScoresFromDB(): Unit = {
    scores.clear()
    val loadedScores = DB readOnly { implicit session =>
      sql"""
      SELECT playerName, turnCount
      FROM Leaderboard
      ORDER BY turnCount ASC
    """
        .map(rs => (rs.string("playerName"), rs.int("turnCount")))
        .list.apply()
    }
    scores ++= loadedScores
  }

  def deleteScore(playerName: String, turnCount: Int): Try[Int] = {
    Try(DB autoCommit { implicit session =>
      sql"""
        DELETE FROM Leaderboard
        WHERE playerName = $playerName AND turnCount = $turnCount
      """.update.apply()
    }).map { result =>
      loadScoresFromDB()
      result
    }
  }

}