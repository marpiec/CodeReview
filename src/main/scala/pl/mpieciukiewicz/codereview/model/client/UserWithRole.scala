package pl.mpieciukiewicz.codereview.model.client

import pl.mpieciukiewicz.codereview.model.constant.ProjectRole
import pl.mpieciukiewicz.codereview.utils.protectedid.ProtectedId

case class UserWithRole(userId: ProtectedId, userName: String, role: ProjectRole)
