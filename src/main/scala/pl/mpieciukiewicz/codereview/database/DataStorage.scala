package pl.mpieciukiewicz.codereview.database

import pl.mpieciukiewicz.codereview.utils.DatabaseAccessor
import pl.mpieciukiewicz.codereview.model.User

class DataStorage(url: String, user: String, password: String) {

  val dba = new DatabaseAccessor(url, user, password)
  import dba._

  def initDatabaseStructure() {
    updateNoParams("CREATE TABLE IF NOT EXISTS user (id INT NOT NULL PRIMARY KEY, name VARCHAR(255), password VARCHAR(255), salt VARCHAR(255), email VARCHAR(255), role VARCHAR(255))")
    updateNoParams("CREATE SEQUENCE IF NOT EXISTS user_seq")
  }


  def addUser(user: User) {
    update("INSERT INTO user (id, name, password, salt, email, role) VALUES (NEXTVAL('user_seq'), ?, ?, ?, ?, ?)") { preparedStatement =>
      preparedStatement.setString(1, user.name)
      preparedStatement.setString(2, user.password)
      preparedStatement.setString(3, user.salt)
      preparedStatement.setString(4, user.email)
      preparedStatement.setString(5, user.role)
    }
  }

  def getUserByName(userName: String):Option[User] = {
    select("SELECT password, salt, email, role FROM user WHERE name = ?") { preparedStatement =>
      preparedStatement.setString(1, userName)
    } { resultSet =>
      if(resultSet.next()) {
        Some(User(userName, resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4)))
      } else {
        None
      }
    }
  }


}
