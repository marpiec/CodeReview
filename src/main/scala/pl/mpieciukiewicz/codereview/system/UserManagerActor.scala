package pl.mpieciukiewicz.codereview.system

import akka.actor.Actor
import pl.mpieciukiewicz.codereview.database.UserStorage
import pl.mpieciukiewicz.codereview.model.User
import pl.mpieciukiewicz.codereview.model.authorization.{SessionInfoClientSide, SessionInfo}
import pl.mpieciukiewicz.codereview.utils.{PasswordUtil, RandomGenerator}
import org.joda.time.Duration
import pl.mpieciukiewicz.codereview.utils.clock.Clock
import scala.util.{Failure, Success}


object UserManagerActor {

  case class RegisterUser(name: String, email: String, password: String)

  case class RegistrationResult(userRegistered: Boolean)


  case class AuthenticateUser(user: String, password: String, ip: String)
  case class Logout(sessionId: String)

  case class AuthenticationResult(userAuthenticated: Boolean, sessionInfo: Option[SessionInfoClientSide] = None)


  case class CheckSession(sessionId: String, ip: String)
  case class CheckSessionResponse(userId: Option[Int])
}


class UserManagerActor(worker: UserManager) extends Actor {

  import UserManagerActor._

  override def receive = {
    case msg: RegisterUser => worker.registerUser(msg.name, msg.email, msg.password) match {
      case true => sender ! RegistrationResult(true)
      case false => sender ! RegistrationResult(false)
    }
    case msg: AuthenticateUser => worker.authenticateUser(msg.user, msg.password, msg.ip) match {
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
