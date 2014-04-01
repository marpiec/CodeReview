package pl.mpieciukiewicz.codereview.database

import org.scalatest.{BeforeAndAfter, GivenWhenThen, FeatureSpec, FunSuite}
import collection.JavaConverters._
import org.fest.assertions.api.Assertions._
import pl.mpieciukiewicz.codereview.model.User
import pl.mpieciukiewicz.codereview.database.engine.{DatabaseAccessor, DocumentDataStorage}
import pl.mpieciukiewicz.codereview.utils.json.JsonUtil
import pl.mpieciukiewicz.codereview.TestsUtil

/**
 *
 */
class UserStorageSpec extends FeatureSpec with GivenWhenThen with BeforeAndAfter {

  var storage:UserStorage = _

  before {
    storage = new UserStorage(new DocumentDataStorage(new DatabaseAccessor(TestsUtil.randomMemoryH2Url, "sa", "sa"), new JsonUtil))
    storage.dds.initDatabaseStructure()
  }

  after {
    storage.dds.close()
  }

  feature("Properly storing user data") {

    scenario("Storing and getting back correct users") {
      Given("Initialized data storage")

      When("Users are stored")
      val prototypeA = User(None, "Marcin", "m.p@mp.pl", "pass123", "abc")
      val prototypeB = User(None, "John", "j.s@mp.pl", "321pass", "123")
      val entityA = storage.add(prototypeA)
      val entityB = storage.add(prototypeB)

      Then("Data storage contains correct users in order they were stored")
      assertThat(storage.loadAll().asJava).containsExactly(entityA, entityB)

    }

    scenario("Storing and getting back correct users by name or email") {
      Given("Initialized data storage")

      When("Users are stored")
      val prototypeA = User(None, "Marcin", "m.p@mp.pl", "pass123", "abc")
      val prototypeB = User(None, "John", "j.s@mp.pl", "321pass", "123")
      val entityA = storage.add(prototypeA)
      val entityB = storage.add(prototypeB)

      Then("Users can be found by name")
      assertThat(storage.findByName("Marcin").get).isEqualTo(entityA)
      assertThat(storage.findByName("John").get).isEqualTo(entityB)
      assertThat(storage.findByName("Jesse").isEmpty).isTrue

      assertThat(storage.findByEmail("m.p@mp.pl").get).isEqualTo(entityA)
      assertThat(storage.findByEmail("j.s@mp.pl").get).isEqualTo(entityB)
      assertThat(storage.findByEmail("w.w@mp.pl").isEmpty).isTrue
    }
  }
}
