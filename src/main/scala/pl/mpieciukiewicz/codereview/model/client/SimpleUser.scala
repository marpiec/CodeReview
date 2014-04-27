package pl.mpieciukiewicz.codereview.model.client

import pl.mpieciukiewicz.codereview.utils.protectedid.ProtectedId

case class SimpleUser(userId: ProtectedId, name: String, email: String)
