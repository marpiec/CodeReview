package pl.mpieciukiewicz.codereview.model

import org.joda.time.DateTime
import pl.marpiec.mpjsons.annotation.FirstSubType
import scala.annotation.meta.field

/**
 *
 */
case class Repository(@(FirstSubType @field)(classOf[Int]) id: Option[Int],
                      projectId: Int,
                      name: String,
                      remoteUrl: String,
                      localDir: String,
                      lastUpdateTime: DateTime)
