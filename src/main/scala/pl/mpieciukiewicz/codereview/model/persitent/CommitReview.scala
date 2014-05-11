package pl.mpieciukiewicz.codereview.model.persitent

import org.joda.time.DateTime
import pl.marpiec.mpjsons.annotation.FirstSubType
import scala.annotation.meta.field

/**
 *
 */
case class CommitReview(@(FirstSubType @field)(classOf[Int]) id: String,
                        commitId: String,
                        reviewerId: String,
                        positive: Boolean,
                        time: DateTime)
