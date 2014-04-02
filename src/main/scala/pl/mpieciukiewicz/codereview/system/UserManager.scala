package pl.mpieciukiewicz.codereview.system

import akka.actor.Actor
import pl.mpieciukiewicz.codereview.database.UserStorage
import pl.mpieciukiewicz.codereview.model.User
import pl.mpieciukiewicz.codereview.model.authorization.{SessionInfoClientSide, SessionInfo}
import pl.mpieciukiewicz.codereview.utils.{PasswordUtil, RandomGenerator}
import org.joda.time.{Duration, DateTime}
import akka.actor.FSM.->
import pl.mpieciukiewicz.codereview.utils.clock.Clock


object UserManager {

  case class RegisterUser(name: String, email: String, password: String)

  case class RegistrationResult(userRegistered: Boolean)


  case class AuthenticateUser(user: String, password: String)
  case class Logout(sessionId: String)

  case class AuthenticationResult(userAuthenticated: Boolean, sessionInfo: Option[SessionInfoClientSide] = None)


  case class GetSessionInfo(sessionId: String)
  case class SessionInfoResponse(sessionInfo: Option[SessionInfo])
}


class UserManager(userStorage: UserStorage, randomUtil: RandomGenerator, clock: Clock, passwordUtil: PasswordUtil) extends Actor {

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
      val salt = passwordUtil.generateRandomSalt
      val passwordHash = passwordUtil.hashPassword(msg.password, salt)
      userStorage.add(User(None, msg.name, msg.email, passwordHash, salt))
      sender ! RegistrationResult(true)
    } else {
      sender ! RegistrationResult(false)
    }

  }

  private def authenticateUser(msg: AuthenticateUser) {

    val userOption = userStorage.findByName(msg.user) orElse userStorage.findByEmail(msg.user)



    if (userOption.isDefined) {
      val user = userOption.get
      val passwordHash = passwordUtil.hashPassword(msg.password, user.salt)
      if (user.passwordHash == passwordHash) {
        var sessionId = ""
        do {
          sessionId = randomUtil.generateSessionIdentifier
        } while (sessions.contains(sessionId))
        val now = clock.now
        val sessionInfo = SessionInfo(user.id.get, user.name, "whoKnows", now, now)
        sessions += sessionId -> sessionInfo

        sender ! AuthenticationResult(true, Some(SessionInfoClientSide(sessionId, sessionInfo.userName)))
      } else {
        sender ! AuthenticationResult(false)
      }
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
    val now = clock.now
    sessions.get(sessionId) match {
      case Some(info) => if(info.lastAction.plus(timeout).isBefore(now)) {
        sessions -= sessionId
        None
      } else {
        val updatedInfo = info.copy(lastAction = now)
        sessions += sessionId -> updatedInfo
        Some(updatedInfo)
      }

      case None => None
    }
  }
}
