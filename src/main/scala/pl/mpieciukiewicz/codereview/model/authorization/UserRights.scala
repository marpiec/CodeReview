package pl.mpieciukiewicz.codereview.model.authorization

import org.joda.time.DateTime

/**
 *
 */
case class UserRights(userName: String, role: String, logged: DateTime, lastAction: DateTime)
