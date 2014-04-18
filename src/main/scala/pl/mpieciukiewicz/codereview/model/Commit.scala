package pl.mpieciukiewicz.codereview.model

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
                  commiter: String, //TODO check if this is unique
                  author: String, //TODO check if this is unique)
                  message: String,
                  branchName: String)
