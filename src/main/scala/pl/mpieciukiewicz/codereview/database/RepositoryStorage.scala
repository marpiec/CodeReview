package pl.mpieciukiewicz.codereview.database

import pl.mpieciukiewicz.codereview.database.engine.DatabaseAccessor
import org.joda.time.DateTime
import java.sql.{Timestamp, ResultSet, PreparedStatement}
import pl.mpieciukiewicz.codereview.model.persitent.Repository

/**
 *
 */
class RepositoryStorage(val dba: DatabaseAccessor, sequenceManager: SequenceManager) extends SimpleStorage[Repository](
  dba, sequenceManager, "repository",
  List("id" -> "INT NOT NULL PRIMARY KEY",
    "project_id" -> "INT NOT NULL",
    "name" -> "VARCHAR(255) NOT NULL",
    "remote_url" -> "VARCHAR(1023) NOT NULL",
    "local_dir" -> "VARCHAR(255) NOT NULL",
    "last_update_time" -> "TIMESTAMP NOT NULL")) {

  override protected def injectId(entity: Repository, id: Int): Repository = entity.copy(id = Some(id))


  override protected def mapEntityToPrepareStatement(entity: Repository, preparedStatement: PreparedStatement) {
    preparedStatement.setInt(1, entity.id.get)
    preparedStatement.setInt(2, entity.projectId)
    preparedStatement.setString(3, entity.name)
    preparedStatement.setString(4, entity.remoteUrl)
    preparedStatement.setString(5, entity.localDir)
    preparedStatement.setTimestamp(6, new Timestamp(entity.lastUpdateTime.getMillis))
  }

  override protected def mapResultToEntity(result: ResultSet): Repository = {
    Repository(Some(result.getInt(1)),
      result.getInt(2),
      result.getString(3),
      result.getString(4),
      result.getString(5),
      new DateTime(result.getTimestamp(6).getTime))
  }

  override def loadAll() = super.loadAll()

  def findByProject(projectId: Int): List[Repository] = {
    findMultipleBy("project_id = ?") {
      preparedStatement => preparedStatement.setInt(1, projectId)
    }
  }

}
