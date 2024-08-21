package aknightodyssey.util
import scalikejdbc._
import aknightodyssey.model.Leaderboard

trait Database {
  val derbyDriverClassname = "org.apache.derby.jdbc.EmbeddedDriver"

  val dbURL = "jdbc:derby:myDB;create=true;";
  // initialize JDBC driver & connection pool
  Class.forName(derbyDriverClassname)
//Configure Username and Password
  ConnectionPool.singleton(dbURL, "AKnightOdyssey", "22043897")

  // ad-hoc session provider on the REPL
  implicit val session = AutoSession

}

object Database extends Database{
  def setupDB() = {
    if (!hasDBInitialize)
      Leaderboard.initializeTable()
  }
  def hasDBInitialize : Boolean = {

    DB getTable "Leaderboard" match {
      case Some(x) => true
      case None => false
    }

  }
}