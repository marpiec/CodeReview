package pl.mpieciukiewicz.codereview.database

import pl.mpieciukiewicz.codereview.database.engine.{DatabaseAccessor, DocumentDataStorage}
import pl.mpieciukiewicz.codereview.model.Commit
import org.joda.time.DateTime
import java.sql.{Timestamp, PreparedStatement, ResultSet}


class CommitStorage(val dba: DatabaseAccessor, sequenceManager: SequenceManager) extends SimpleStorage[Commit](
  dba, sequenceManager, "commit",
  List("id" -> "INT NOT NULL PRIMARY KEY",
    "repository_id" -> "INT NOT NULL",
    "time" -> "TIMESTAMP NOT NULL",
    "hash" -> "VARCHAR(255) NOT NULL",
    "commiter" -> "VARCHAR(255) NOT NULL",
    "commiter_email" -> "VARCHAR(255) NOT NULL",
    "author" -> "VARCHAR(255) NOT NULL",
    "author_email" -> "VARCHAR(255) NOT NULL",
    "message" -> "VARCHAR(255) NOT NULL")) {


  override protected def mapEntityToPrepareStatement(entity: Commit, preparedStatement: PreparedStatement) {
    preparedStatement.setInt(1, entity.id.get)
    preparedStatement.setInt(2, entity.repositoryId)
    preparedStatement.setTimestamp(3, new Timestamp(entity.time.getMillis))
    preparedStatement.setString(4, entity.hash)
    preparedStatement.setString(5, entity.commiter)
    preparedStatement.setString(6, entity.commiterEmail)
    preparedStatement.setString(7, entity.author)
    preparedStatement.setString(8, entity.authorEmail)
    preparedStatement.setString(9, entity.message)
  }

  override protected def mapResultToEntity(resultSet: ResultSet): Commit = {
    Commit(Some(resultSet.getInt(1)),
      resultSet.getInt(2),
      new DateTime(resultSet.getTimestamp(3).getTime),
      resultSet.getString(4),
      resultSet.getString(5),
      resultSet.getString(6),
      resultSet.getString(7),
      resultSet.getString(8),
      resultSet.getString(9))
  }

  override protected def injectId(entity: Commit, id: Int): Commit = entity.copy(id = Some(id))

  override def loadAll() = super.loadAll()

  def findByRepositoryId(repositoryId: Int):List[Commit] = {
    findMultipleBy("repository_id = ?") {
      preparedStatement => preparedStatement.setInt(1, repositoryId)
    }
  }

  def findByRepositoryId(repositoryId: Int, from: Int, count: Int):List[Commit] = {
    findMultipleBy("repository_id = ?") {
      preparedStatement => preparedStatement.setInt(1, repositoryId)
    }.drop(from).take(count) //TODO implement on db level
  }


}
