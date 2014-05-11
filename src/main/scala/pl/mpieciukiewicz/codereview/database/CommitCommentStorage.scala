package pl.mpieciukiewicz.codereview.database

import pl.mpieciukiewicz.codereview.database.engine.DatabaseAccessor
import java.sql.{ResultSet, Timestamp, PreparedStatement}
import org.joda.time.DateTime
import pl.mpieciukiewicz.codereview.model.CommitComment

class CommitCommentStorage(val dba: DatabaseAccessor, sequenceManager: SequenceManager) extends SimpleStorage[CommitComment](
  dba, sequenceManager, "commit",
  List("id" -> "INT NOT NULL PRIMARY KEY",
    "commit_id" -> "INT NOT NULL",
    "user_id" -> "INT NOT NULL",
    "time" -> "TIMESTAMP NOT NULL",
    "comment" -> "VARCHAR(1023) NOT NULL",
    "response_to_id" -> "INT"
    )) with OptionSupport {


  override protected def mapEntityToPrepareStatement(entity: CommitComment, preparedStatement: PreparedStatement) {
    preparedStatement.setInt(1, entity.id)
    preparedStatement.setInt(2, entity.commitId)
    preparedStatement.setInt(3, entity.userId)
    preparedStatement.setTimestamp(4, new Timestamp(entity.time.getMillis))
    preparedStatement.setString(5, entity.comment)
    putIntOption(preparedStatement, 6, entity.responseToId)
  }

  override protected def mapResultToEntity(resultSet: ResultSet): CommitComment = {
    CommitComment(
      id = resultSet.getInt(1),
      commitId = resultSet.getInt(2),
      userId = resultSet.getInt(3),
      time = new DateTime(resultSet.getTimestamp(4).getTime),
      comment = resultSet.getString(5),
      responseToId = toIntOption(resultSet.getInt(6)))
  }


  override protected def injectId(entity: CommitComment, id: Int): CommitComment = entity.copy(id = id)

  override def loadAll() = super.loadAll()
}
