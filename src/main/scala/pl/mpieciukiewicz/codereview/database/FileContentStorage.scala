package pl.mpieciukiewicz.codereview.database

import pl.mpieciukiewicz.codereview.database.engine.{DatabaseAccessor, DocumentDataStorage}
import pl.mpieciukiewicz.codereview.model.{LineChange, Commit, Repository, FileContent}
import pl.mpieciukiewicz.codereview.model.constant.FileChangeType
import java.sql.{ResultSet, Timestamp, PreparedStatement}
import org.joda.time.DateTime
import pl.mpieciukiewicz.codereview.utils.json.JsonUtil

class FileContentStorage(val dba: DatabaseAccessor, sequenceManager: SequenceManager, jsonUtil: JsonUtil) extends SimpleStorage[FileContent](
  dba, sequenceManager, "file_content",
  List("id" -> "INT NOT NULL PRIMARY KEY",
    "commit_id" -> "INT NOT NULL",
    "from_content" -> "CLOB",
    "from_path" -> "VARCHAR(255)",
    "to_content" -> "CLOB",
    "to_path" -> "VARCHAR(255)",
    "line_changes" -> "CLOB NOT NULL",
    "change_type" -> "VARCHAR(255) NOT NULL")) {

  override protected def injectId(entity: FileContent, id: Int): FileContent = entity.copy(id = id)

  override protected def mapEntityToPrepareStatement(entity: FileContent, preparedStatement: PreparedStatement) {
    preparedStatement.setInt(1, entity.id)
    preparedStatement.setInt(2, entity.commitId)
    preparedStatement.setString(3, entity.fromContent.getOrElse(null))
    preparedStatement.setString(4, entity.fromPath.getOrElse(null))
    preparedStatement.setString(5, entity.toContent.getOrElse(null))
    preparedStatement.setString(6, entity.toPath.getOrElse(null))
    preparedStatement.setString(7, jsonUtil.toJson(entity.lineChanges))
    preparedStatement.setString(8, entity.changeType.getName)
  }

  override protected def mapResultToEntity(resultSet: ResultSet): FileContent = {
    FileContent(resultSet.getInt(1),
      resultSet.getInt(2),
      Option(resultSet.getString(3)),
      Option(resultSet.getString(4)),
      Option(resultSet.getString(5)),
      Option(resultSet.getString(6)),
      jsonUtil.fromJsonGeneric(resultSet.getString(7), classOf[List[LineChange]], classOf[FileContent].getDeclaredField("lineChanges")),
      FileChangeType.getByName(resultSet.getString(8)))
  }


  def findByCommit(commitId: Int): List[FileContent] = {
    findMultipleBy("commit_id = ?") {
      preparedStatement => preparedStatement.setInt(1, commitId)
    }
  }

}
