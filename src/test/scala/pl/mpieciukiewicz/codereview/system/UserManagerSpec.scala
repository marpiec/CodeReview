package pl.mpieciukiewicz.codereview.system

import org.scalatest._
import collection.JavaConverters._
import org.fest.assertions.api.Assertions._
import pl.mpieciukiewicz.codereview.database.UserStorage
import pl.mpieciukiewicz.codereview.utils.{RandomGenerator, PasswordUtil}
import pl.mpieciukiewicz.codereview.utils.clock.SettableStagnantClock
import pl.mpieciukiewicz.codereview.TestsUtil._
import pl.mpieciukiewicz.codereview.utils.email.NoOpMailSender

/**
 *
 */
class UserManagerSpec extends FeatureSpecLike with GivenWhenThen with BeforeAndAfter {

  var userManager: UserManager = null
  var userStorage: UserStorage = _
  var clock: SettableStagnantClock = null

  before {
    clock = new SettableStagnantClock
    userStorage = new UserStorage(createTemporaryDataAccessor, new UniqueMemorySequenceManager)
    userManager = new UserManager(userStorage, new RandomGenerator, clock, new PasswordUtil("systemSalt"), new NoOpMailSender)
  }

  after {
    userStorage.dba.close()
  }




  feature("Can register and authenticate user correctly") {


    scenario("User registration") {
      Given("User Manager with empty user storage")

      When("First user is registered")
      var registrationResult = userManager.registerUser("Marcin", "m.p@g.p", "mySecret")

      Then("One user exists in data storage")

      assertThat(registrationResult).isTrue
      assertThat(userStorage.loadAll().asJava).hasSize(1)
      assertThat(userStorage.findByNameOrEmail("Marcin", "").isDefined)

      When("Second user is registered")
      registrationResult = userManager.registerUser("John", "j.s@g.p", "MyPass")

      Then("Two users exists in data storage")

      assertThat(registrationResult).isTrue
      assertThat(userStorage.loadAll().asJava).hasSize(2)
      assertThat(userStorage.findByNameOrEmail("John", "").isDefined)

      When("User with the same name as first one is registered")
      registrationResult = userManager.registerUser("Marcin", "fake.m.p@g.p", "MyOtherSecret")

      Then("Registration is unsuccessful")
      assertThat(registrationResult).isFalse

      When("User with the same email as first one is registered")
      registrationResult = userManager.registerUser("nicraM", "m.p@g.p", "MyOtherSecret")

      Then("Registration is unsuccessful")
      assertThat(registrationResult).isFalse

    }


    scenario("Correct user authentication") {
      Given("User Manager with one user registered")
      userManager.registerUser("Marcin", "m.p@g.p", "mySecret")

      When("User tries to authenticate with correct name and password")
      var authenticationResult = userManager.authenticateUser("Marcin", "mySecret", "127.0.0.1")

      Then("Is successfully registered with correct id")
      assertThat(authenticationResult.isSuccess).isTrue
      assertThat(authenticationResult.get.userName).isEqualTo("Marcin")

      When("User tries to authenticate with correct email and password")
      authenticationResult = userManager.authenticateUser("m.p@g.p", "mySecret", "192.168.0.2")

      Then("Is successfully authenticate with correct id")
      assertThat(authenticationResult.isSuccess).isTrue
      assertThat(authenticationResult.get.userName).isEqualTo("Marcin")

    }



    scenario("Incorrect user authentication") {
      Given("User Manager with one user registered")
      userManager.registerUser("Marcin", "m.p@g.p", "mySecret")

      When("User tries to authenticate with incorrect password")
      var authenticationResult = userManager.authenticateUser("Marcin", "iDontKnow", "127.0.0.1")

      Then("Is denied of registration")
      assertThat(authenticationResult.isFailure).isTrue

      When("User tries to authenticate with incorrect name or email")
      authenticationResult = userManager.authenticateUser("John", "mySecret", "192.168.0.2")

      Then("Is denied of registration")
      assertThat(authenticationResult.isFailure).isTrue
    }
  }


  feature("Session management") {


    scenario("User logs in and logs out") {
      Given("Registered user")
      var response = userManager.registerUser("Marcin", "m.p@g.p", "mySecret")

      When("User logs in correctly and session id is retrieved")
      val authenticationResult = userManager.authenticateUser("m.p@g.p", "mySecret", "192.168.0.2")
      val sessionId = authenticationResult.get.sessionId

      Then("Session info with user id is retrieved when getting it by session id")
      var userIdOption = userManager.checkSession(sessionId, "192.168.0.2")
      assertThat(userIdOption.isDefined).isTrue

      When("User logs out")
      userManager.logout(sessionId)

      Then("Session info without user id is retrieved when getting it by session id")
      userIdOption = userManager.checkSession(sessionId, "192.168.0.2")
      assertThat(userIdOption.isDefined).isFalse

    }

    scenario("User logs in and session times out") {
      Given("Registered user, and new session id")
      userManager.registerUser("Marcin", "m.p@g.p", "mySecret")
      val authenticationResult = userManager.authenticateUser("m.p@g.p", "mySecret", "192.168.0.2")
      val sessionId = authenticationResult.get.sessionId

      When("15 minutes 01 second passes")
      clock.plusMinutes(15).plusSeconds(1)

      Then("Session is invalid")
      val userIdOption = userManager.checkSession(sessionId, "192.168.0.2")
      assertThat(userIdOption.isDefined).isFalse
    }

    scenario("User logs in has some activity then session times out") {
      Given("Registered user, and new session id")
      userManager.registerUser("Marcin", "m.p@g.p", "mySecret")
      val authenticationResult = userManager.authenticateUser("m.p@g.p", "mySecret", "192.168.0.2")
      val sessionId = authenticationResult.get.sessionId

      When("14 minutes 59 second passes")
      clock.plusMinutes(14).plusSeconds(59)

      Then("Session is valid")
      var userIdOption = userManager.checkSession(sessionId, "192.168.0.2")
      assertThat(userIdOption.isDefined).isTrue

      When("14 again minutes 59 second passes")
      clock.plusMinutes(14).plusSeconds(59)

      Then("Session is valid")
      userIdOption = userManager.checkSession(sessionId, "192.168.0.2")
      assertThat(userIdOption.isDefined).isTrue

      When("15 minutes 01 second passes")
      clock.plusMinutes(15).plusSeconds(1)

      Then("Session is invalid")
      userIdOption = userManager.checkSession(sessionId, "192.168.0.2")
      assertThat(userIdOption.isDefined).isFalse

    }

    scenario("User logs in, and then some request with the session id occurs from different host") {
      Given("Registered user, and new session id")
      userManager.registerUser("Marcin", "m.p@g.p", "mySecret")
      val authenticationResult = userManager.authenticateUser("m.p@g.p", "mySecret", "192.168.0.2")
      val sessionId = authenticationResult.get.sessionId

      When("User has some activity from proper id")
      var userIdOption = userManager.checkSession(sessionId, "192.168.0.2")

      Then("Session is valid")
      assertThat(userIdOption.isDefined).isTrue

      When("Same session id is used from different host")
      userIdOption = userManager.checkSession(sessionId, "100.100.100.1")

      Then("Session is invalidated")
      assertThat(userIdOption.isDefined).isFalse

      When("Same session id is used from correct host")
      userIdOption = userManager.checkSession(sessionId, "192.168.0.2")

      Then("Session was invalidated after improper access")
      assertThat(userIdOption.isDefined).isFalse

    }

  }

}
