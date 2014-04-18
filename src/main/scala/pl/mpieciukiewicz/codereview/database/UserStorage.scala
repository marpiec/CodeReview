package pl.mpieciukiewicz.codereview.database

import pl.mpieciukiewicz.codereview.model.User
import pl.mpieciukiewicz.codereview.database.engine.{DatabaseAccessor, DocumentDataStorage}
import pl.mpieciukiewicz.codereview.model.constant.SystemRole
import java.sql.ResultSet

/**
 *
 */
class UserStorage(val dba: DatabaseAccessor, val sequenceManager: SequenceManager) {

  import dba._

  updateNoParams(
    "CREATE TABLE IF NOT EXISTS user (" +
      "id INT NOT NULL PRIMARY KEY, " +
      "name VARCHAR(255) NOT NULL, " +
      "email VARCHAR(255) NOT NULL, " +
      "password_hash VARCHAR(255) NOT NULL, " +
      "salt VARCHAR(255) NOT NULL, " +
      "system_role VARCHAR(255) NOT NULL)"
  )

  updateNoParams("CREATE SEQUENCE IF NOT EXISTS user_seq INCREMENT 20")

  def add(user: User): User = {
    val newUser = user.copy(id = Some(sequenceManager.nextUserId()))
    update("INSERT INTO user (id, name, email, password_hash, salt, system_role) VALUES (?, ?, ?, ?, ?, ?)") {
      prepareStatement =>
        prepareStatement.setInt(1, newUser.id.get)
        prepareStatement.setString(2, newUser.name)
        prepareStatement.setString(3, newUser.email)
        prepareStatement.setString(4, newUser.passwordHash)
        prepareStatement.setString(5, newUser.salt)
        prepareStatement.setString(6, newUser.systemRole.getName)
    }
    newUser
  }

  def loadAll(): List[User] = {
    selectNoParams("SELECT id, name, email, password_hash, salt, system_role FROM user") { resultSet =>
      mapResultSetToUsers(resultSet)
    }
  }

  def findByNameOrEmail(name: String, email: String): Option[User] = {
    select("SELECT id, name, email, password_hash, salt, system_role FROM user WHERE name = ? OR email = ?") { preparedStatement =>
      preparedStatement.setString(1, name)
      preparedStatement.setString(2, email)
    } { resultSet =>
      if(resultSet.next()) {
        Some(mapResultToUser(resultSet))
      } else {
        None
      }
    }

  }

  private def mapResultSetToUsers(resultSet: ResultSet): List[User] = {
    var users = List[User]()
    while (resultSet.next()) {
      users ::= mapResultToUser(resultSet)
    }
    users.reverse
  }

  private def mapResultToUser(result: ResultSet): User = {
    User(Some(result.getInt(1)),
      result.getString(2),
      result.getString(3),
      result.getString(4),
      result.getString(5),
      SystemRole.apply(result.getString(6)))
  }
}
