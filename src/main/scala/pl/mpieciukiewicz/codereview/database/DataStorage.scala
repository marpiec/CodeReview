package pl.mpieciukiewicz.codereview.database

import pl.mpieciukiewicz.codereview.utils.DatabaseAccessor

class DataStorage(url: String, user: String, password: String) extends DatabaseAccessor(url, user, password) {
//

  def initDatabaseStructure() {
    db {
      connection =>
        withStatement(connection) {
          statement =>
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS repository (id INT NOT NULL PRIMARY KEY, name VARCHAR)")

            statement.executeUpdate("CREATE SEQUENCE IF NOT EXISTS repository_seq")
        }
    }
  }




}
