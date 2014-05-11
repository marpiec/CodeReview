package pl.mpieciukiewicz.codereview.model

import pl.marpiec.mpjsons.annotation.FirstSubType
import scala.annotation.meta.field
import pl.mpieciukiewicz.codereview.model.constant.BeforeOrAfter
import org.joda.time.DateTime


case class FileComment(commitId: Int,
                       fileId: Int,
                       beforeOrAfter: BeforeOrAfter,
                       userId: Int,
                       time: DateTime,
                       comment: String,
                       @(FirstSubType @field)(classOf[Int]) responseToId: Option[Int],
                       id: Int = 0)
