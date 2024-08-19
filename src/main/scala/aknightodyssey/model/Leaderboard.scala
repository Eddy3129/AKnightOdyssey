package aknightodyssey.model

import scalafx.collections.ObservableBuffer
import aknightodyssey.util.Database
import scalikejdbc._

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
        insert into Leaderboard (playerName, turnCount)
        values (${playerName}, ${turnCount})
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
      sql"select playerName, turnCount from Leaderboard order by turnCount asc"
        .map(rs => (rs.string("playerName"), rs.int("turnCount")))
        .list.apply()
    }
    scores ++= loadedScores
  }

  def clearLeaderboard(): Unit = {
    scores.clear()
  }
}
