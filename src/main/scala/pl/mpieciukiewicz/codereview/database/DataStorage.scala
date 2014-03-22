package pl.mpieciukiewicz.codereview.database

import pl.mpieciukiewicz.codereview.utils.{JsonUtil, DatabaseAccessor}
import pl.mpieciukiewicz.codereview.model.User

class DataStorage(url: String, user: String, password: String) {

  val jsonUtil = new JsonUtil

  val dba = new DatabaseAccessor(url, user, password)
  import dba._

  def initDatabaseStructure() {
    updateNoParams("CREATE TABLE IF NOT EXISTS user (id INT NOT NULL PRIMARY KEY, object CLOB)")
    updateNoParams("CREATE SEQUENCE IF NOT EXISTS user_seq")
  }


  def addUser(user: User): User = {
    val newUserId: Int = selectNoParams("SELECT NEXTVAL('user_seq')") { resultSet =>
      resultSet.next()
      resultSet.getInt(1)
    }

    val userWithId = user.copy(id = newUserId)
    update("INSERT INTO user (id, object) VALUES (?, ?)") { preparedStatement =>
      preparedStatement.setInt(1, newUserId)
      preparedStatement.setString(2, jsonUtil.toJson(userWithId))
    }

    userWithId
  }
  
  def loadAllUsers():List[User] = {
    selectNoParams("SELECT object FROM user ORDER BY id") { resultSet =>
      var users = List[User]()
      while(resultSet.next()) {
        users ::= jsonUtil.fromJson(resultSet.getString(1), classOf[User])
      }
      users.reverse
    }
  }

  def findUserByName(userName: String):Option[User] = {
    selectNoParams("SELECT object FROM user") { resultSet =>
      while(resultSet.next()) {
        val user = jsonUtil.fromJson(resultSet.getString(1), classOf[User])
        if(user.name == userName) {
          return Some(user)
        }
      }
      return None
    }
  }
  
  def close() {
    dba.close()
  }


}
