package pl.mpieciukiewicz.codereview.model

import pl.marpiec.mpjsons.annotation.FirstSubType
import scala.annotation.meta.field

/**
 *
 */
case class User(@(FirstSubType @field)(classOf[Int]) id: Option[Int],
                name: String,
                email: String,
                password: String,
                salt: String)


