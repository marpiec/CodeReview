package pl.mpieciukiewicz.codereview.model

import org.joda.time.DateTime
import pl.marpiec.mpjsons.annotation.FirstSubType
import scala.annotation.meta.field

/**
 *
 */
case class LineComment(@(FirstSubType @field)(classOf[Int]) id: Option[Int],
                       commitId: Int,
                       userId: Int,
                       fileId: Int,
                       after: Boolean, //TODO: indicates if line is in line before change or after,  change it somehow.
                       lineNumber: Int,
                       time: DateTime,
                       comment: String,
                       @(FirstSubType @field)(classOf[Int]) responseToId: Option[Int])
