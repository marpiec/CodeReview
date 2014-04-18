package pl.mpieciukiewicz.codereview.model

import pl.marpiec.mpjsons.annotation.FirstSubType
import scala.annotation.meta.field
import pl.mpieciukiewicz.codereview.model.constant.SystemRole

/**
 *
 */
case class User(@(FirstSubType @field)(classOf[Int]) id: Option[Int],
                name: String,
                email: String,
                passwordHash: String,
                salt: String,
                systemRole: SystemRole)



