package pl.mpieciukiewicz.codereview.database

import java.sql._
import com.citi.marsrt.totalrecon.config.Configuration
import scala.Some
import com.citi.marsrt.totalrecon.utils.SqlUtils._
import com.citi.marsrt.totalrecon.model.XVar

object DataStorage {


  def initDatabaseStructure() {
    mainDB {
      connection =>
        withStatement(connection) {
          statement =>
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS repository (id INT NOT NULL PRIMARY KEY, name VARCHAR)")

            statement.executeUpdate("CREATE SEQUENCE IF NOT EXISTS repository_seq")
        }
    }
  }


  def mainDatabaseUrl = {
    "jdbc:h2:c:/test"
  }


  private def mainDB[T](codeBlock: (Connection) => T): T = {
    db(mainDatabaseUrl)(codeBlock)
  }



}
