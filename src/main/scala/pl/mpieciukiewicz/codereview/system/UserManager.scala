package pl.mpieciukiewicz.codereview.system

import pl.mpieciukiewicz.codereview.database.UserStorage
import pl.mpieciukiewicz.codereview.model.User
import pl.mpieciukiewicz.codereview.model.authorization.{SessionInfoClientSide, SessionInfo}
import pl.mpieciukiewicz.codereview.utils.{PasswordUtil, RandomGenerator}
import org.joda.time.Duration
import pl.mpieciukiewicz.codereview.utils.clock.Clock
import scala.util.{Failure, Success, Try}
import pl.mpieciukiewicz.codereview.model.constant.SystemRole


class UserManager(userStorage: UserStorage, randomUtil: RandomGenerator, clock: Clock, passwordUtil: PasswordUtil) {

  var sessions = Map[String, SessionInfo]()
  var timeout = Duration.standardMinutes(15)

  def registerUser(name: String, email: String, password: String): Boolean = {

    if (userStorage.findByNameOrEmail(name, email).isEmpty) {
      val salt = passwordUtil.generateRandomSalt
      val passwordHash = passwordUtil.hashPassword(password, salt)
      userStorage.add(User(None, name, email, passwordHash, salt, SystemRole.User))
      true
    } else {
      false
    }

  }

  def authenticateUser(user: String, password: String, ip: String): Try[SessionInfoClientSide] = {

    val userOption = userStorage.findByNameOrEmail(user, user)

    if (userOption.isDefined) {
      val user = userOption.get
      val passwordHash = passwordUtil.hashPassword(password, user.salt)
      if (user.passwordHash == passwordHash) {
        var sessionId = ""
        do {
          sessionId = randomUtil.generateSessionIdentifier
        } while (sessions.contains(sessionId))
        val now = clock.now
        val sessionInfo = SessionInfo(user.id.get, user.name, "whoKnows", ip, now, now)
        sessions += sessionId -> sessionInfo

        Success(SessionInfoClientSide(sessionId, sessionInfo.userName))
      } else {
        Failure(new IncorrectPasswordException)
      }
    } else {
      Failure(new NoSuchUserException)
    }
  }


  def logout(sessionId: String) {
    sessions -= sessionId
  }

  def checkSession(sessionId: String, ip: String): Option[Int] = {
    val now = clock.now
    sessions.get(sessionId) match {
      case Some(info) => if (info.lastAction.plus(timeout).isAfter(now) && info.ip == ip) {
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
