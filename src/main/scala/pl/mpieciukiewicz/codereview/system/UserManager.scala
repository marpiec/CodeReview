package pl.mpieciukiewicz.codereview.system

import akka.actor.Actor
import pl.mpieciukiewicz.codereview.database.UserStorage
import pl.mpieciukiewicz.codereview.model.User
import pl.mpieciukiewicz.codereview.model.authorization.{SessionInfoClientSide, SessionInfo}
import pl.mpieciukiewicz.codereview.utils.RandomUtil
import org.joda.time.{Duration, DateTime}
import akka.actor.FSM.->


object UserManager {

  case class RegisterUser(name: String, email: String, password: String)

  case class RegistrationResult(userRegistered: Boolean)


  case class AuthenticateUser(user: String, password: String)
  case class Logout(sessionId: String)

  case class AuthenticationResult(userAuthenticated: Boolean, sessionInfo: Option[SessionInfoClientSide] = None)


  case class GetSessionInfo(sessionId: String)
  case class SessionInfoResponse(sessionInfo: Option[SessionInfo])
}


class UserManager(userStorage: UserStorage, randomUtil: RandomUtil) extends Actor {

  import UserManager._

  var sessions = Map[String, SessionInfo]()
  var timeout = Duration.standardMinutes(15)


  override def receive = {
    case msg: RegisterUser => registerUser(msg)
    case msg: AuthenticateUser => authenticateUser(msg)
    case msg: Logout => logout(msg)
    case msg: GetSessionInfo => sessionInfo(msg)
  }

  private def registerUser(msg: RegisterUser) {

    if (userStorage.findByName(msg.name).isEmpty && userStorage.findByEmail(msg.email).isEmpty) {
      userStorage.add(User(None, msg.name, msg.email, msg.password, "salt"))
      sender ! RegistrationResult(true)
    } else {
      sender ! RegistrationResult(false)
    }

  }

  private def authenticateUser(msg: AuthenticateUser) {

    val user = userStorage.findByName(msg.user) orElse userStorage.findByEmail(msg.user)

    if (user.isDefined && user.get.password == msg.password) {
      var sessionId = ""
      do {
        sessionId = randomUtil.generateSessionIdentifier
      } while (sessions.contains(sessionId))
      val now = DateTime.now
      val sessionInfo = SessionInfo(user.get.id.get, user.get.name, "whoKnows", now, now)
      sessions += sessionId -> sessionInfo

      sender ! AuthenticationResult(true, Some(SessionInfoClientSide(sessionId, sessionInfo.userName)))
    } else {
      sender ! AuthenticationResult(false)
    }
  }


  private def logout(msg: Logout) {
    sessions -= msg.sessionId
    sender ! AuthenticationResult(false)
  }

  private def sessionInfo(msg: GetSessionInfo) {
     sender ! SessionInfoResponse(readSessionInfo(msg.sessionId))
  }


  private def readSessionInfo(sessionId: String): Option[SessionInfo] = {
    sessions.get(sessionId) match {
      case Some(info) => if(info.lastAction.plus(timeout).isBefore(DateTime.now)) {
        sessions -= sessionId
        None
      } else {
        val updatedInfo = info.copy(lastAction = DateTime.now)
        sessions += sessionId -> updatedInfo
        Some(updatedInfo)
      }

      case None => None
    }
  }
}
