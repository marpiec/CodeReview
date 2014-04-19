package pl.mpieciukiewicz.codereview.system

import akka.testkit.{TestKit, ImplicitSender, TestActorRef, DefaultTimeout}
import akka.pattern.ask
import org.scalatest._
import collection.JavaConverters._
import org.fest.assertions.api.Assertions._
import akka.actor.{ActorSystem, PoisonPill}
import pl.mpieciukiewicz.codereview.database.UserStorage
import pl.mpieciukiewicz.codereview.utils.{PasswordUtil, RandomGenerator}
import pl.mpieciukiewicz.codereview.utils.clock.SettableStagnantClock
import pl.mpieciukiewicz.codereview.TestsUtil._
import pl.mpieciukiewicz.codereview.system.actor.UserManagerActor
import pl.mpieciukiewicz.codereview.system.actor.UserManagerActor.{CheckSessionResponse, AuthenticationResult}

/**
 *
 */
class UserManagerActorSpec extends TestKit(ActorSystem("test")) with FeatureSpecLike with GivenWhenThen with BeforeAndAfter with ImplicitSender with DefaultTimeout {

  var userManager: TestActorRef[UserManagerActor] = null
  var userStorage: UserStorage = _
  var clock: SettableStagnantClock = null

  before {
    clock = new SettableStagnantClock
    userStorage = new UserStorage(createTemporaryDataAccessor, new UniqueMemorySequenceManager)
    userManager = TestActorRef(new UserManagerActor(new UserManager(userStorage, new RandomGenerator, clock, new PasswordUtil("systemSalt"))))
  }

  after {
    userStorage.dba.close()
    userManager ! PoisonPill
  }




  feature("Can register and authenticate user correctly") {


    scenario("User registration") {
      Given("User Manager with empty user storage")

      When("First user is registered")
      var response = userManager ? UserManagerActor.RegisterUser("Marcin", "m.p@g.p", "mySecret")

      Then("One user exists in data storage")

      assertThat(response.value.get.get).isEqualTo(UserManagerActor.RegistrationResult(true))
      assertThat(userStorage.loadAll().asJava).hasSize(1)
      assertThat(userStorage.findByNameOrEmail("Marcin", "").isDefined)

      When("Second user is registered")
      response = userManager ? UserManagerActor.RegisterUser("John", "j.s@g.p", "MyPass")

      Then("Two users exists in data storage")

      assertThat(response.value.get.get).isEqualTo(UserManagerActor.RegistrationResult(true))
      assertThat(userStorage.loadAll().asJava).hasSize(2)
      assertThat(userStorage.findByNameOrEmail("John", "").isDefined)

      When("User with the same name as first one is registered")
      response = userManager ? UserManagerActor.RegisterUser("Marcin", "fake.m.p@g.p", "MyOtherSecret")

      Then("Registration is unsuccessful")
      assertThat(response.value.get.get).isEqualTo(UserManagerActor.RegistrationResult(false))

      When("User with the same email as first one is registered")
      response = userManager ? UserManagerActor.RegisterUser("nicraM", "m.p@g.p", "MyOtherSecret")

      Then("Registration is unsuccessful")
      assertThat(response.value.get.get).isEqualTo(UserManagerActor.RegistrationResult(false))

    }


    scenario("Correct user authentication") {
      Given("User Manager with one user registered")
      var response = userManager ? UserManagerActor.RegisterUser("Marcin", "m.p@g.p", "mySecret")

      When("User tries to authenticate with correct name and password")
      response = userManager ? UserManagerActor.AuthenticateUser("Marcin", "mySecret", "127.0.0.1")

      Then("Is successfully registered with correct id")
      var result = response.value.get.get.asInstanceOf[AuthenticationResult]
      assertThat(result.userAuthenticated).isTrue
      assertThat(result.sessionInfo.get.userName).isEqualTo("Marcin")

      When("User tries to authenticate with correct email and password")
      response = userManager ? UserManagerActor.AuthenticateUser("m.p@g.p", "mySecret", "192.168.0.2")

      Then("Is successfully authenticate with correct id")
      result = response.value.get.get.asInstanceOf[AuthenticationResult]
      assertThat(result.userAuthenticated).isTrue
      assertThat(result.sessionInfo.get.userName).isEqualTo("Marcin")

    }



    scenario("Incorrect user authentication") {
      Given("User Manager with one user registered")
      var response = userManager ? UserManagerActor.RegisterUser("Marcin", "m.p@g.p", "mySecret")

      When("User tries to authenticate with incorrect password")
      response = userManager ? UserManagerActor.AuthenticateUser("Marcin", "iDontKnow", "127.0.0.1")

      Then("Is denied of registration")
      assertThat(response.value.get.get).isEqualTo(UserManagerActor.AuthenticationResult(false))

      When("User tries to authenticate with incorrect name or email")
      response = userManager ? UserManagerActor.AuthenticateUser("John", "mySecret", "192.168.0.2")

      Then("Is denied of registration")
      assertThat(response.value.get.get).isEqualTo(UserManagerActor.AuthenticationResult(false))
    }
  }


  feature("Session management") {


    scenario("User logs in and logs out") {
      Given("Registered user")
      var response = userManager ? UserManagerActor.RegisterUser("Marcin", "m.p@g.p", "mySecret")

      When("User logs in correctly and session id is retrieved")
      response = userManager ? UserManagerActor.AuthenticateUser("m.p@g.p", "mySecret", "192.168.0.2")
      val sessionId = response.value.get.get.asInstanceOf[AuthenticationResult].sessionInfo.get.sessionId

      Then("Session info with user id is retrieved when getting it by session id")
      response = userManager ? UserManagerActor.CheckSession(sessionId, "192.168.0.2")
      var userIdOption = response.value.get.get.asInstanceOf[CheckSessionResponse].userId
      assertThat(userIdOption.isDefined).isTrue

      When("User logs out")
      userManager ? UserManagerActor.Logout(sessionId)

      Then("Session info without user id is retrieved when getting it by session id")
      response = userManager ? UserManagerActor.CheckSession(sessionId, "192.168.0.2")
      userIdOption = response.value.get.get.asInstanceOf[CheckSessionResponse].userId
      assertThat(userIdOption.isDefined).isFalse

    }

    scenario("User logs in and session times out") {
      Given("Registered user, and new session id")
      var response = userManager ? UserManagerActor.RegisterUser("Marcin", "m.p@g.p", "mySecret")
      response = userManager ? UserManagerActor.AuthenticateUser("m.p@g.p", "mySecret", "192.168.0.2")
      val sessionId = response.value.get.get.asInstanceOf[AuthenticationResult].sessionInfo.get.sessionId

      When("15 minutes 01 second passes")
      clock.plusMinutes(15).plusSeconds(1)

      Then("Session is invalid")
      response = userManager ? UserManagerActor.CheckSession(sessionId, "192.168.0.2")
      val userIdOption = response.value.get.get.asInstanceOf[CheckSessionResponse].userId
      assertThat(userIdOption.isDefined).isFalse
    }

    scenario("User logs in has some activity then session times out") {
      Given("Registered user, and new session id")
      var response = userManager ? UserManagerActor.RegisterUser("Marcin", "m.p@g.p", "mySecret")
      response = userManager ? UserManagerActor.AuthenticateUser("m.p@g.p", "mySecret", "192.168.0.2")
      val sessionId = response.value.get.get.asInstanceOf[AuthenticationResult].sessionInfo.get.sessionId

      When("14 minutes 59 second passes")
      clock.plusMinutes(14).plusSeconds(59)

      Then("Session is valid")
      response = userManager ? UserManagerActor.CheckSession(sessionId, "192.168.0.2")
      var userIdOption = response.value.get.get.asInstanceOf[CheckSessionResponse].userId
      assertThat(userIdOption.isDefined).isTrue

      When("14 again minutes 59 second passes")
      clock.plusMinutes(14).plusSeconds(59)

      Then("Session is valid")
      response = userManager ? UserManagerActor.CheckSession(sessionId, "192.168.0.2")
      userIdOption = response.value.get.get.asInstanceOf[CheckSessionResponse].userId
      assertThat(userIdOption.isDefined).isTrue

      When("15 minutes 01 second passes")
      clock.plusMinutes(15).plusSeconds(1)

      Then("Session is invalid")
      response = userManager ? UserManagerActor.CheckSession(sessionId, "192.168.0.2")
      userIdOption = response.value.get.get.asInstanceOf[CheckSessionResponse].userId
      assertThat(userIdOption.isDefined).isFalse

    }

    scenario("User logs in, and then some request with the session id occurs from different host") {
      Given("Registered user, and new session id")
      var response = userManager ? UserManagerActor.RegisterUser("Marcin", "m.p@g.p", "mySecret")
      response = userManager ? UserManagerActor.AuthenticateUser("m.p@g.p", "mySecret", "192.168.0.2")
      val sessionId = response.value.get.get.asInstanceOf[AuthenticationResult].sessionInfo.get.sessionId

      When("User has some activity from proper id")
      response = userManager ? UserManagerActor.CheckSession(sessionId, "192.168.0.2")
      var userIdOption = response.value.get.get.asInstanceOf[CheckSessionResponse].userId

      Then("Session is valid")
      assertThat(userIdOption.isDefined).isTrue

      When("Same session id is used from different host")
      response = userManager ? UserManagerActor.CheckSession(sessionId, "100.100.100.1")
      userIdOption = response.value.get.get.asInstanceOf[CheckSessionResponse].userId

      Then("Session is invalidated")
      assertThat(userIdOption.isDefined).isFalse

      When("Same session id is used from correct host")
      response = userManager ? UserManagerActor.CheckSession(sessionId, "192.168.0.2")
      userIdOption = response.value.get.get.asInstanceOf[CheckSessionResponse].userId

      Then("Session was invalidated after improper access")
      assertThat(userIdOption.isDefined).isFalse

    }

  }

}
