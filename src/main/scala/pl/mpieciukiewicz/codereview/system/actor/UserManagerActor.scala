package pl.mpieciukiewicz.codereview.system.actor

import akka.actor.Actor
import pl.mpieciukiewicz.codereview.model.authorization.SessionInfoClientSide
import scala.util.{Failure, Success}
import pl.mpieciukiewicz.codereview.system.UserManager


object UserManagerActor {

  case class RegisterUser(name: String, email: String)
  case class RegistrationResult(userRegistered: Boolean)

  case class ChangeUserPassword(userId: Int, oldPassword: String, newPassword: String)
  case class ChangePasswordResult(passwordChanged: Boolean)

  case class ForgotPassword(user: String)

  case class AuthenticateUser(user: String, password: String, ip: String)
  case class AuthenticationResult(userAuthenticated: Boolean, sessionInfo: Option[SessionInfoClientSide] = None)

  case class Logout(sessionId: String)

  case class CheckSession(sessionId: String, ip: String)
  case class CheckSessionResponse(userId: Option[Int])

}


class UserManagerActor(worker: UserManager) extends Actor {

  import UserManagerActor._

  override def receive = {
    case msg: RegisterUser => worker.registerUser(msg.name, msg.email) match {
      case true => sender ! RegistrationResult(true)
      case false => sender ! RegistrationResult(false)
    }
    case msg: ChangeUserPassword => worker.changeUserPassword(msg.userId, msg.oldPassword, msg.newPassword) match {
      case Success(_) => sender ! ChangePasswordResult(true)
      case Failure(_) => sender ! ChangePasswordResult(false)
    }
    case msg: ForgotPassword =>
      worker.forgotPassword(msg.user)
    case msg: AuthenticateUser =>
      println(worker)
      worker.authenticateUser(msg.user, msg.password, msg.ip) match {
      case Success(sessionInfo) => sender ! AuthenticationResult(true, Some(sessionInfo))
      case Failure(_) => sender ! AuthenticationResult(false)
    }
    case msg: Logout =>
      worker.logout(msg.sessionId)
      sender ! AuthenticationResult(false)
    case msg: CheckSession =>
      val userIdOption = worker.checkSession(msg.sessionId, msg.ip)
      sender ! CheckSessionResponse(userIdOption)
  }
}
