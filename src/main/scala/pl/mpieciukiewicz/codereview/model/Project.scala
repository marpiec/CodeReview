package pl.mpieciukiewicz.codereview.model

import pl.marpiec.mpjsons.annotation.FirstSubType
import scala.annotation.meta.field

/**
 *
 */
case class Project(@(FirstSubType @field)(classOf[Int]) id: Option[Int],
                   name: String)
