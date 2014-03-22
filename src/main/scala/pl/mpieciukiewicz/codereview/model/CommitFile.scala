package pl.mpieciukiewicz.codereview.model

import pl.mpieciukiewicz.codereview.model.constant.FileChangeType

/**
 *
 */
case class CommitFile(id: Int,
                      fromPath: Option[String],
                      toPath: Option[String],
                      changeType: FileChangeType)