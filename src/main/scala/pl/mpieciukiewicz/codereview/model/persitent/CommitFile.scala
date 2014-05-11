package pl.mpieciukiewicz.codereview.model.persitent

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
