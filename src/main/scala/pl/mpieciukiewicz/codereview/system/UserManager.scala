package pl.mpieciukiewicz.codereview.system

import akka.actor.Actor
import pl.mpieciukiewicz.codereview.database.UserStorage
import pl.mpieciukiewicz.codereview.model.User


object UserManager {

  case class RegisterUser(name: String, email: String, password: String)

  case class RegistrationResult(userRegistered: Boolean)


  case class AuthenticateUser(user: String, password: String)
  case class AuthenticationResult(userAuthenticated: Boolean, userId: Option[Int] = None, userName: Option[String] = None)

}


class UserManager(userStorage: UserStorage) extends Actor {

  import UserManager._

  override def receive = {
    case msg: RegisterUser => registerUser(msg)
    case msg: AuthenticateUser => authenticateUser(msg)
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

    if(user.isDefined && user.get.password == msg.password) {
      sender ! AuthenticationResult(true, Some(user.get.id.get), Some(user.get.name))
    } else {
      sender ! AuthenticationResult(false)
    }
  }

}
