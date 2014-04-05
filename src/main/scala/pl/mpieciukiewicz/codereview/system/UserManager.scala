package pl.mpieciukiewicz.codereview.system

import akka.actor.Actor
import pl.mpieciukiewicz.codereview.database.UserStorage
import pl.mpieciukiewicz.codereview.model.User
import pl.mpieciukiewicz.codereview.model.authorization.{SessionInfoClientSide, SessionInfo}
import pl.mpieciukiewicz.codereview.utils.{PasswordUtil, RandomGenerator}
import org.joda.time.Duration
import pl.mpieciukiewicz.codereview.utils.clock.Clock


object UserManager {

  case class RegisterUser(name: String, email: String, password: String)

  case class RegistrationResult(userRegistered: Boolean)


  case class AuthenticateUser(user: String, password: String, ip: String)
  case class Logout(sessionId: String)

  case class AuthenticationResult(userAuthenticated: Boolean, sessionInfo: Option[SessionInfoClientSide] = None)


  case class GetSessionInfo(sessionId: String, ip: String)
  case class SessionInfoResponse(userId: Option[Int])
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
        val sessionInfo = SessionInfo(user.id.get, user.name, "whoKnows", msg.ip, now, now)
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
     sender ! SessionInfoResponse(checkSession(msg.sessionId, msg.ip))
  }


  private def checkSession(sessionId: String, ip: String): Option[Int] = {
    val now = clock.now
    sessions.get(sessionId) match {
      case Some(info) => if(info.lastAction.plus(timeout).isAfter(now) && info.ip == ip) {
          val updatedInfo = info.copy(lastAction = now)
          sessions += sessionId -> updatedInfo
          Some(updatedInfo.userId)
        } else {
          sessions -= sessionId
          None
        }
      case None => None
    }
  }
}
