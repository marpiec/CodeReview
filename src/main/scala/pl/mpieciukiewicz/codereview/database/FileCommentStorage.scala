package pl.mpieciukiewicz.codereview.database

import pl.mpieciukiewicz.codereview.database.engine.DatabaseAccessor
import java.sql.{ResultSet, Timestamp, PreparedStatement}
import pl.mpieciukiewicz.codereview.model.FileComment
import org.joda.time.DateTime
import pl.mpieciukiewicz.codereview.model.constant.BeforeOrAfter

class FileCommentStorage(val dba: DatabaseAccessor, sequenceManager: SequenceManager) extends SimpleStorage[FileComment](
  dba, sequenceManager, "commit",
  List("id" -> "INT NOT NULL PRIMARY KEY",
    "commit_id" -> "INT NOT NULL",
    "file_id" -> "INT NOT NULL",
    "before_or_after" -> "VARCHAR(255) NOT NULL",
    "user_id" -> "INT NOT NULL",
    "time" -> "TIMESTAMP NOT NULL",
    "comment" -> "VARCHAR(1023) NOT NULL",
    "response_to_id" -> "INT"
    )) with OptionSupport {


  override protected def mapEntityToPrepareStatement(entity: FileComment, preparedStatement: PreparedStatement) {
    preparedStatement.setInt(1, entity.id)
    preparedStatement.setInt(2, entity.commitId)
    preparedStatement.setInt(3, entity.fileId)
    preparedStatement.setString(4, entity.beforeOrAfter.getName)
    preparedStatement.setInt(5, entity.userId)
    preparedStatement.setTimestamp(6, new Timestamp(entity.time.getMillis))
    preparedStatement.setString(7, entity.comment)
    putIntOption(preparedStatement, 8, entity.responseToId)
  }

  override protected def mapResultToEntity(resultSet: ResultSet): FileComment = {
    FileComment(
      id = resultSet.getInt(1),
      commitId = resultSet.getInt(2),
      fileId = resultSet.getInt(3),
      beforeOrAfter = BeforeOrAfter.getByName(resultSet.getString(4)),
      userId = resultSet.getInt(5),
      time = new DateTime(resultSet.getTimestamp(6).getTime),
      comment = resultSet.getString(7),
      responseToId = toIntOption(resultSet.getInt(8)))
  }


  override protected def injectId(entity: FileComment, id: Int): FileComment = entity.copy(id = id)

  override def loadAll() = super.loadAll()

  def findByCommitId(commitId: Int): List[FileComment] = {
    findMultipleBy("commit_id = ?") { preparedStatement =>
      preparedStatement.setInt(1, commitId)
    }
  }
}
