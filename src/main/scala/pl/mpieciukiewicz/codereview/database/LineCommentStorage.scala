package pl.mpieciukiewicz.codereview.database

import pl.mpieciukiewicz.codereview.database.engine.DatabaseAccessor
import java.sql.{ResultSet, Timestamp, PreparedStatement}
import org.joda.time.DateTime
import pl.mpieciukiewicz.codereview.model.constant.BeforeOrAfter
import pl.mpieciukiewicz.codereview.model.persitent.LineComment

class LineCommentStorage(val dba: DatabaseAccessor, sequenceManager: SequenceManager) extends SimpleStorage[LineComment](
  dba, sequenceManager, "commit",
  List("id" -> "INT NOT NULL PRIMARY KEY",
    "commit_id" -> "INT NOT NULL",
    "file_id" -> "INT NOT NULL",
    "before_or_after" -> "VARCHAR(255) NOT NULL",
    "line_number" -> "INT NOT NULL",
    "user_id" -> "INT NOT NULL",
    "time" -> "TIMESTAMP NOT NULL",
    "comment" -> "VARCHAR(1023) NOT NULL",
    "response_to_id" -> "INT"
  )) with OptionSupport {


  override protected def mapEntityToPrepareStatement(entity: LineComment, preparedStatement: PreparedStatement) {
    preparedStatement.setInt(1, entity.id)
    preparedStatement.setInt(2, entity.commitId)
    preparedStatement.setInt(3, entity.fileId)
    preparedStatement.setString(4, entity.beforeOrAfter.getName)
    preparedStatement.setInt(5, entity.lineNumber)
    preparedStatement.setInt(6, entity.userId)
    preparedStatement.setTimestamp(7, new Timestamp(entity.time.getMillis))
    preparedStatement.setString(8, entity.comment)
    putIntOption(preparedStatement, 9, entity.responseToId)
  }

  override protected def mapResultToEntity(resultSet: ResultSet): LineComment = {
    LineComment(
      id = resultSet.getInt(1),
      commitId = resultSet.getInt(2),
      fileId = resultSet.getInt(3),
      beforeOrAfter = BeforeOrAfter.getByName(resultSet.getString(4)),
      lineNumber = resultSet.getInt(5),
      userId = resultSet.getInt(6),
      time = new DateTime(resultSet.getTimestamp(7).getTime),
      comment = resultSet.getString(8),
      responseToId = toIntOption(resultSet.getInt(9)))
  }


  override protected def injectId(entity: LineComment, id: Int): LineComment = entity.copy(id = id)

  override def loadAll() = super.loadAll()

  def findByCommitId(commitId: Int): List[LineComment] = {
    findMultipleBy("commit_id = ?") { preparedStatement =>
      preparedStatement.setInt(1, commitId)
    }
  }
}
