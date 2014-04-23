package pl.mpieciukiewicz.codereview.database

import pl.mpieciukiewicz.codereview.model.{UserRole, Project, User}
import pl.mpieciukiewicz.codereview.database.engine.{DatabaseAccessor, DocumentDataStorage}
import pl.mpieciukiewicz.codereview.model.constant.SystemRole
import java.sql.{PreparedStatement, ResultSet}

/**
 *
 */
class UserStorage(val dba: DatabaseAccessor, sequenceManager: SequenceManager)
  extends SimpleStorage[User] (dba, sequenceManager,  "user", List("id" -> "INT NOT NULL PRIMARY KEY",
    "name" -> "VARCHAR(255) NOT NULL",
    "email" -> "VARCHAR(255) NOT NULL",
    "password_hash" -> "VARCHAR(255) NOT NULL",
    "salt" -> "VARCHAR(255) NOT NULL",
    "system_role" -> "VARCHAR(255) NOT NULL")) {


  override protected def injectId(entity: User, id: Int): User = entity.copy(id = Some(id))

  override protected def mapEntityToPrepareStatement(entity: User, preparedStatement: PreparedStatement) {
    preparedStatement.setInt(1, entity.id.get)
    preparedStatement.setString(2, entity.name)
    preparedStatement.setString(3, entity.email)
    preparedStatement.setString(4, entity.passwordHash)
    preparedStatement.setString(5, entity.salt)
    preparedStatement.setString(6, entity.systemRole.getName)
  }

  override protected def mapResultToEntity(result: ResultSet): User = {
    User(Some(result.getInt(1)),
      result.getString(2),
      result.getString(3),
      result.getString(4),
      result.getString(5),
      SystemRole.apply(result.getString(6)))
  }

  override def loadAll(): List[User] = {
    super.loadAll()
  }

  def findByNameOrEmail(name: String, email: String): Option[User] = {
    findSingleBy("name = ? OR email = ?") {
      preparedStatement =>
        preparedStatement.setString(1, name)
        preparedStatement.setString(2, email)
    }
  }

  def update(entity: User) {
    updateEntityOption(entity)
  }

}
