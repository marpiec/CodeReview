package pl.mpieciukiewicz.codereview.model.authorization

import org.joda.time.DateTime

case class SessionInfo(userName: String, role: String, logged: DateTime, lastAction: DateTime)

case class SessionInfoClientSide(sessionId: String, userName: String)
