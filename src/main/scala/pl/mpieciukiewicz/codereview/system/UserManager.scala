package pl.mpieciukiewicz.codereview.system

import akka.actor.Actor
import pl.mpieciukiewicz.codereview.database.UserStorage
import pl.mpieciukiewicz.codereview.model.User
import pl.mpieciukiewicz.codereview.model.authorization.UserRights
import pl.mpieciukiewicz.codereview.utils.RandomUtil
import org.joda.time.DateTime


object UserManager {

  case class RegisterUser(name: String, email: String, password: String)

  case class RegistrationResult(userRegistered: Boolean)


  case class AuthenticateUser(user: String, password: String)
  case class CheckSession(sessionId: String)

  case class AuthenticationResult(userAuthenticated: Boolean, sessionId: Option[String] = None, userRights: Option[UserRights] = None)

}


class UserManager(userStorage: UserStorage, randomUtil: RandomUtil) extends Actor {

  import UserManager._

  var sessions = Map[String, UserRights]()


  override def receive = {
    case msg: RegisterUser => registerUser(msg)
    case msg: AuthenticateUser => authenticateUser(msg)
    case msg: CheckSession => checkSession(msg)
  }

  private def registerUser(msg: RegisterUser) = {

    if (userStorage.findByName(msg.name).isEmpty && userStorage.findByEmail(msg.email).isEmpty) {
      userStorage.add(User(None, msg.name, msg.email, msg.password, "salt"))
      sender ! RegistrationResult(true)
    } else {
      sender ! RegistrationResult(false)
    }

  }

  private def authenticateUser(msg: AuthenticateUser) = {

    val user = userStorage.findByName(msg.user) orElse userStorage.findByEmail(msg.user)

    if (user.isDefined && user.get.password == msg.password) {
      var sessionId = ""
      do {
        sessionId = randomUtil.generateSessionIdentifier
      } while (sessions.contains(sessionId))
      val now = DateTime.now
      val userRights = UserRights(user.get.name, "whoKnows", now, now)
      sessions += sessionId -> userRights

      sender ! AuthenticationResult(true, Some(sessionId), Some(userRights))
    } else {
      sender ! AuthenticationResult(false)
    }
  }

  private def checkSession(msg: CheckSession) = {
     sessions.get(msg.sessionId) match {
       case Some(userRights) => sender ! AuthenticationResult(true, Some(msg.sessionId), Some(userRights))
       case None => sender ! AuthenticationResult(false)
     }
  }

}
