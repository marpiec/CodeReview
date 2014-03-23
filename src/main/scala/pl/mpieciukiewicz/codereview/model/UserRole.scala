package pl.mpieciukiewicz.codereview.model

import pl.mpieciukiewicz.codereview.model.constant.ProjectRole
import pl.marpiec.mpjsons.annotation.FirstSubType
import scala.annotation.meta.field

/**
 *
 */
case class UserRole(@(FirstSubType @field)(classOf[Int]) id: Option[Int],
                    userId: Int,
                    projectId: Int,
                    role: ProjectRole)
