package pl.mpieciukiewicz.codereview.database

import pl.mpieciukiewicz.codereview.model.{User, UserRole, Repository}
import pl.mpieciukiewicz.codereview.database.engine.{DatabaseAccessor, DocumentDataStorage}
import java.sql.{PreparedStatement, ResultSet}
import pl.mpieciukiewicz.codereview.model.constant.{ProjectRole, SystemRole}

/**
 *
 */
class UserRoleStorage(val dba: DatabaseAccessor, sequenceManager: SequenceManager) extends SimpleStorage[UserRole](
  dba, sequenceManager, "user_role",
  List("id" -> "INT NOT NULL PRIMARY KEY",
    "user_id" -> "INT NOT NULL",
    "project_id" -> "INT NOT NULL",
    "role" -> "VARCHAR(255) NOT NULL")) {

  override protected def injectId(entity: UserRole, id: Int): UserRole = entity.copy(id = id)

  override protected def mapEntityToPrepareStatement(entity: UserRole, preparedStatement: PreparedStatement) {
    preparedStatement.setInt(1, entity.id)
    preparedStatement.setInt(2, entity.userId)
    preparedStatement.setInt(3, entity.projectId)
    preparedStatement.setString(4, entity.role.getName)
  }

  override protected def mapResultToEntity(result: ResultSet): UserRole = {
    UserRole(result.getInt(1),
            result.getInt(2),
            result.getInt(3),
            ProjectRole.getByName(result.getString(4)))
  }

  override def loadAll():List[UserRole] = {
    super.loadAll()
  }

  def findByUser(userId: Int): List[UserRole] = {
    findMultipleBy("user_id = ?") {
      preparedStatement => preparedStatement.setInt(1, userId)
    }
  }

  def findByProject(projectId: Int): List[UserRole] = {
    findMultipleBy("project_id = ?") {
      preparedStatement => preparedStatement.setInt(1, projectId)
    }
  }

  def findByUserAndProject(userId: Int, projectId: Int): Option[UserRole] = {
    findSingleBy("user_id = ? AND project_id = ?") {
      preparedStatement =>
        preparedStatement.setInt(1, userId)
        preparedStatement.setInt(2, projectId)
    }
  }

  def removeByProjectAndUser(userId: Int, projectId: Int) {
    removeBy("user_id = ? AND project_id = ?") {
      preparedStatement =>
        preparedStatement.setInt(1, userId)
        preparedStatement.setInt(2, projectId)
    }
  }


  def update(entity: UserRole) {
    updateEntity(entity)
  }

}
