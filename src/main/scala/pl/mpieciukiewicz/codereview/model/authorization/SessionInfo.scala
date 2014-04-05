package pl.mpieciukiewicz.codereview.model.authorization

import org.joda.time.DateTime

case class SessionInfo(userId: Int, userName: String, role: String, ip: String, logged: DateTime, lastAction: DateTime)

case class SessionInfoClientSide(sessionId: String, userName: String)
