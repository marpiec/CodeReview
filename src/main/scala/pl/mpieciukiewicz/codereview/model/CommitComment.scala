package pl.mpieciukiewicz.codereview.model

import pl.marpiec.mpjsons.annotation.FirstSubType
import scala.annotation.meta.field
import org.joda.time.DateTime

case class CommitComment(commitId: Int,
                         userId: Int,
                         time: DateTime,
                         comment: String,
                         @(FirstSubType @field)(classOf[Int]) responseToId: Option[Int],
                         id: Int = 0)
