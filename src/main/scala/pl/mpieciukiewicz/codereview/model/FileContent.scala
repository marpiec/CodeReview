package pl.mpieciukiewicz.codereview.model

import pl.mpieciukiewicz.codereview.model.constant.FileChangeType

case class FileContent(id: Int,
                       commitId: Int,
                       fromContent: Option[String], fromPath: Option[String],
                       toContent: Option[String], toPath: Option[String],
                       lineChanges: List[LineChange],
                       changeType: FileChangeType)
