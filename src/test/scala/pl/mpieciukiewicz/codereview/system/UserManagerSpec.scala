package pl.mpieciukiewicz.codereview.system

import akka.testkit.{TestKit, ImplicitSender, TestActorRef,DefaultTimeout}
import akka.pattern.ask
import org.scalatest._
import collection.JavaConverters._
import org.fest.assertions.api.Assertions._
import akka.actor.{ActorSystem, PoisonPill}
import pl.mpieciukiewicz.codereview.database.UserStorage
import pl.mpieciukiewicz.codereview.database.engine.{DatabaseAccessor, DocumentDataStorage}
import pl.mpieciukiewicz.codereview.utils.json.JsonUtil

/**
 *
 */
class UserManagerSpec extends TestKit(ActorSystem("test")) with FeatureSpecLike with GivenWhenThen with BeforeAndAfter with ImplicitSender with DefaultTimeout{

  var userManager:TestActorRef[UserManager] = _
  var userStorage:UserStorage = _


  before {
    val documentDataStorage = new DocumentDataStorage(new DatabaseAccessor("jdbc:h2:mem:testdb", "sa", "sa"), new JsonUtil)
    documentDataStorage.initDatabaseStructure()
    userStorage = new UserStorage(documentDataStorage)
    userManager = TestActorRef(new UserManager(userStorage))
  }

  after {
    userStorage.dds.close()
    userManager ! PoisonPill
  }

  feature("Can register and authenticate user correctly") {

    scenario("User registration") {
      Given("User Manager with empty user storage")

      When("First user is registered")
      var response = userManager ? UserManager.RegisterUser("Marcin", "m.p@g.p", "mySecret")

      Then("One user exists in data storage")

      assertThat(response.value.get.get).isEqualTo(UserManager.RegistrationResult(true))
      assertThat(userStorage.loadAll().asJava).hasSize(1)
      assertThat(userStorage.findByName("Marcin").isDefined)

      When("Second user is registered")
      response = userManager ? UserManager.RegisterUser("John", "j.s@g.p", "MyPass")

      Then("Two users exists in data storage")

      assertThat(response.value.get.get).isEqualTo(UserManager.RegistrationResult(true))
      assertThat(userStorage.loadAll().asJava).hasSize(2)
      assertThat(userStorage.findByName("John").isDefined)

      When("User with the same name as first one is registered")
      response = userManager ? UserManager.RegisterUser("Marcin", "fake.m.p@g.p", "MyOtherSecret")

      Then("Registration is unsuccessful")
      assertThat(response.value.get.get).isEqualTo(UserManager.RegistrationResult(false))

      When("User with the same email as first one is registered")
      response = userManager ? UserManager.RegisterUser("nicraM", "m.p@g.p", "MyOtherSecret")

      Then("Registration is unsuccessful")
      assertThat(response.value.get.get).isEqualTo(UserManager.RegistrationResult(false))

    }


    scenario("Correct user authentication") {
      Given("User Manager with one user registered")
      var response = userManager ? UserManager.RegisterUser("Marcin", "m.p@g.p", "mySecret")
      val userId = userStorage.findByName("Marcin").get.id.get

      When("User tries to authenticate with correct name and password")
      response = userManager ? UserManager.AuthenticateUser("Marcin", "mySecret")

      Then("Is successfully registered with correct id")
      assertThat(response.value.get.get).isEqualTo(UserManager.AuthenticationResult(true, Some(userId), Some("Marcin")))

      When("User tries to authenticate with correct email and password")
      response = userManager ? UserManager.AuthenticateUser("m.p@g.p", "mySecret")

      Then("Is successfully authenticate with correct id")
      assertThat(response.value.get.get).isEqualTo(UserManager.AuthenticationResult(true, Some(userId), Some("Marcin")))
    }



    scenario("Incorrect user authentication") {
      Given("User Manager with one user registered")
      var response = userManager ? UserManager.RegisterUser("Marcin", "m.p@g.p", "mySecret")

      When("User tries to authenticate with incorrect password")
      response = userManager ? UserManager.AuthenticateUser("Marcin", "iDontKnow")

      Then("Is denied of registration")
      assertThat(response.value.get.get).isEqualTo(UserManager.AuthenticationResult(false))

      When("User tries to authenticate with incorrect name or email")
      response = userManager ? UserManager.AuthenticateUser("John", "mySecret")

      Then("Is denied of registration")
      assertThat(response.value.get.get).isEqualTo(UserManager.AuthenticationResult(false))
    }
  }

}
