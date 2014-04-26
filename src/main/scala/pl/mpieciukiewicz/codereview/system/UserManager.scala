package pl.mpieciukiewicz.codereview.system

import pl.mpieciukiewicz.codereview.database.{UserRoleStorage, UserStorage}
import pl.mpieciukiewicz.codereview.model.User
import pl.mpieciukiewicz.codereview.model.authorization.{SessionInfoClientSide, SessionInfo}
import pl.mpieciukiewicz.codereview.utils.{PasswordUtil, RandomGenerator}
import org.joda.time.Duration
import pl.mpieciukiewicz.codereview.utils.clock.Clock
import scala.util.{Failure, Success, Try}
import pl.mpieciukiewicz.codereview.model.constant.SystemRole
import pl.mpieciukiewicz.codereview.utils.email.MailSender
import pl.mpieciukiewicz.codereview.model.client.UserWithRole
import pl.mpieciukiewicz.codereview.utils.protectedid.ProtectedId


class UserManager(userStorage: UserStorage, randomUtil: RandomGenerator, clock: Clock, passwordUtil: PasswordUtil, mailSender: MailSender,
                   userRoleStorage: UserRoleStorage) {

  var sessions = Map[String, SessionInfo]()
  var timeout = Duration.standardMinutes(15)

  def registerUser(name: String, email: String): Boolean = {

    if (userStorage.findByNameOrEmail(name, email).isEmpty) {
      val password = passwordUtil.generateRandomPassword
      val salt = passwordUtil.generateRandomSalt
      val passwordHash = passwordUtil.hashPassword(password, salt)
      userStorage.add(User(None, name, email, passwordHash, salt, SystemRole.User))

      mailSender.sendMailAsync("User registered", "User registered, temporary password: "+password, email, Map())

      true
    } else {
      false
    }
  }

  def forgotPassword(user: String) {
    userStorage.findByNameOrEmail(user, user) match {
      case Some(userEntity) =>
        val newPassword = passwordUtil.generateRandomPassword
        updateUserPassword(userEntity, newPassword)
        mailSender.sendMailAsync("User registered", "Password has been changed: "+newPassword, userEntity.email, Map())
      case None => ()
    }
  }

  def changeUserPassword(userId: Int, oldPassword: String, newPassword: String): Try[Boolean] = {
    val userOption = userStorage.findById(userId)

    if (userOption.isDefined) {
      val user = userOption.get
      if (checkUserPassword(user, oldPassword)) {
        updateUserPassword(user, newPassword)
        Success(true)
      } else {
        Failure(new IncorrectPasswordException)
      }
    } else {
      Failure(new NoSuchUserException)
    }
  }


  private def updateUserPassword(user: User, newPassword: String) {
    val salt = passwordUtil.generateRandomSalt
    val passwordHash = passwordUtil.hashPassword(newPassword, salt)
    userStorage.update(user.copy(salt = salt, passwordHash = passwordHash))
  }

  def authenticateUser(user: String, password: String, ip: String): Try[SessionInfoClientSide] = {

    val userOption = userStorage.findByNameOrEmail(user, user)

    if (userOption.isDefined) {
      val userEntity = userOption.get
      if (checkUserPassword(userEntity, password)) {
        var sessionId = ""
        do {
          sessionId = randomUtil.generateSessionIdentifier
        } while (sessions.contains(sessionId))
        val now = clock.now
        val sessionInfo = SessionInfo(userEntity.id.get, userEntity.name, "whoKnows", ip, now, now)
        sessions += sessionId -> sessionInfo

        Success(SessionInfoClientSide(sessionId, sessionInfo.userName))
      } else {
        Failure(new IncorrectPasswordException)
      }
    } else {
      Failure(new NoSuchUserException)
    }
  }

  private def checkUserPassword(user: User, password: String): Boolean = {
    val passwordHash = passwordUtil.hashPassword(password, user.salt)
    user.passwordHash == passwordHash
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

  def loadUsersForProject(projectId: Int):List[UserWithRole] = {
    val userRoles = userRoleStorage.findByProject(projectId)
    val users = userStorage.loadUsersByIds(userRoles.map(_.userId))
    val userIdToRole = userRoles.map(role => (role.userId, role.role)).toMap

    users.map(user => UserWithRole(ProtectedId.encrypt(user.id.get), user.name, userIdToRole(user.id.get)))
  }
}
