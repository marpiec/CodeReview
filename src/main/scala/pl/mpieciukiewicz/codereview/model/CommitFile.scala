package pl.mpieciukiewicz.codereview.model

import pl.mpieciukiewicz.codereview.model.constant.FileChangeType
import pl.marpiec.mpjsons.annotation.FirstSubType
import scala.annotation.meta.field

/**
 *
 */
case class CommitFile(@(FirstSubType @field)(classOf[Int]) id: Option[Int],
                      fromPath: Option[String],
                      toPath: Option[String],
                      changeType: FileChangeType)
