package pl.mpieciukiewicz.codereview.model

import org.joda.time.DateTime
import pl.marpiec.mpjsons.annotation.FirstSubType
import scala.annotation.meta.field

/**
 *
 */
case class CommitComment(@(FirstSubType @field)(classOf[Int]) id: Option[Int],
                         commitId: Int,
                         userId: Int,
                         time: DateTime,
                         comment: String,
                         @(FirstSubType @field)(classOf[Int]) responseToId: Option[Int])
