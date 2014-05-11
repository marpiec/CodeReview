package pl.mpieciukiewicz.codereview.model.persitent

import pl.mpieciukiewicz.codereview.model.constant.ProjectRole

/**
 *
 */
case class UserRole(id: Int,
                    userId: Int,
                    projectId: Int,
                    role: ProjectRole)
