package pl.mpieciukiewicz.codereview.database

import org.scalatest.{BeforeAndAfter, GivenWhenThen, FeatureSpec, FunSuite}
import collection.JavaConverters._
import org.fest.assertions.api.Assertions._
import pl.mpieciukiewicz.codereview.model.User
import pl.mpieciukiewicz.codereview.utils.{JsonUtil, DatabaseAccessor}

/**
 *
 */
class ModelStorageSpec extends FeatureSpec with GivenWhenThen with BeforeAndAfter {

  var ms:ModelStorage = _

  before {
    ms = new ModelStorage(new DocumentDataStorage(new DatabaseAccessor("jdbc:h2:mem:testdb", "sa", "sa"), new JsonUtil))
    ms.dds.initDatabaseStructure()
  }

  after {
    ms.dds.close()
  }

  feature("Properly storing user data") {

    scenario("Storing and getting back correct users") {
      Given("Initialized data storage")

      When("Users are stored")
      val userAPrototype = User("Marcin", "m.p@mp.pl", "pass123", "abc")
      val userBPrototype = User("John", "j.s@mp.pl", "321pass", "123")
      val userA = ms.addUser(userAPrototype)
      val userB = ms.addUser(userBPrototype)

      Then("Data storage contains correct users in order they were stored")
      assertThat(ms.loadAllUsers().asJava).containsExactly(userA, userB)

    }

    scenario("Storing and getting back correct users by name") {
      Given("Initialized data storage")

      When("Users are stored")
      val userAPrototype = User("Marcin", "m.p@mp.pl", "pass123", "abc")
      val userBPrototype = User("John", "j.s@mp.pl", "321pass", "123")
      val userA = ms.addUser(userAPrototype)
      val userB = ms.addUser(userBPrototype)

      Then("Users can be found by name")
      assertThat(ms.findUserByName("Marcin").get).isEqualTo(userA)
      assertThat(ms.findUserByName("John").get).isEqualTo(userB)
      assertThat(ms.findUserByName("Jesse").isEmpty).isTrue

    }
  }
}
