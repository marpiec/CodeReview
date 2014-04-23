package pl.mpieciukiewicz.codereview.database

import pl.mpieciukiewicz.codereview.model.{User, Project, Repository}
import pl.mpieciukiewicz.codereview.database.engine.{DatabaseAccessor, DocumentDataStorage}
import java.sql.{PreparedStatement, ResultSet}
import pl.mpieciukiewicz.codereview.model.constant.SystemRole

/**
 *
 */
class ProjectStorage(val dba: DatabaseAccessor, sequenceManager: SequenceManager) extends SimpleStorage[Project](
  dba, sequenceManager, "project",
  List("id" -> "INT NOT NULL PRIMARY KEY",
    "name" -> "VARCHAR(255) NOT NULL")) {

  override protected def injectId(entity: Project, id: Int): Project = entity.copy(id = Some(id))


  override protected def mapEntityToPrepareStatement(entity: Project, preparedStatement: PreparedStatement) {
    preparedStatement.setInt(1, entity.id.get)
    preparedStatement.setString(2, entity.name)
  }

  override protected def mapResultToEntity(result: ResultSet): Project = {
    Project(Some(result.getInt(1)),
      result.getString(2))
  }

  override def loadAll():List[Project] = {
    super.loadAll()
  }

  def findByName(name: String): Option[Project] = {
    findSingleBy("name = ?") {
      preparedStatement => preparedStatement.setString(1, name)
    }
  }

  def loadByProjectsIds(ids: Iterable[Int]): List[Project] = {
    loadManyByIds(ids)
  }


}
