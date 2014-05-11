package pl.mpieciukiewicz.codereview.model.persitent

import org.joda.time.DateTime
import pl.marpiec.mpjsons.annotation.FirstSubType
import scala.annotation.meta.field

/**
 *
 */
case class Commit(@(FirstSubType @field)(classOf[Int]) id: Option[Int],
                  repositoryId: Int,
                  time: DateTime,
                  hash: String,
                  commiter: String,
                  commiterEmail: String,
                  author: String,
                  authorEmail: String,
                  message: String)
