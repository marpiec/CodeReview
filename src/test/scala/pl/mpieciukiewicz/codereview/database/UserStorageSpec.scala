package pl.mpieciukiewicz.codereview.database

import org.scalatest.{BeforeAndAfter, GivenWhenThen, FeatureSpec, FunSuite}
import collection.JavaConverters._
import org.fest.assertions.api.Assertions._
import pl.mpieciukiewicz.codereview.model.User
import pl.mpieciukiewicz.codereview.database.engine.{DatabaseAccessor, DocumentDataStorage}
import pl.mpieciukiewicz.codereview.utils.json.JsonUtil
import pl.mpieciukiewicz.codereview.TestsUtil
import pl.mpieciukiewicz.codereview.TestsUtil._
import pl.mpieciukiewicz.codereview.model.User
import pl.mpieciukiewicz.codereview.model.constant.SystemRole

/**
 *
 */
class UserStorageSpec extends FeatureSpec with GivenWhenThen with BeforeAndAfter {

  var storage:UserStorage = _

  before {
    storage = new UserStorage(createTemporaryDataAccessor, new MemorySequenceManager)
  }

  after {
    storage.dba.close()
  }

  feature("Properly storing user data") {

    scenario("Storing and getting back correct users") {
      Given("Initialized data storage")

      When("Users are stored")
      val prototypeA = User(None, "Marcin", "m.p@mp.pl", "12431234", "abc", SystemRole.Admin)
      val prototypeB = User(None, "John", "j.s@mp.pl", "fweffe", "123", SystemRole.User)
      val entityA = storage.add(prototypeA)
      val entityB = storage.add(prototypeB)

      Then("Data storage contains correct users in order they were stored")
      assertThat(storage.loadAll().asJava).containsExactly(entityA, entityB)

    }

    scenario("Storing and getting back correct users by name or email") {
      Given("Initialized data storage")

      When("Users are stored")
      val prototypeA = User(None, "Marcin", "m.p@mp.pl", "234235", "abc", SystemRole.Admin)
      val prototypeB = User(None, "John", "j.s@mp.pl", "gwergw", "123", SystemRole.User)
      val entityA = storage.add(prototypeA)
      val entityB = storage.add(prototypeB)

      Then("Users can be found by name")
      assertThat(storage.findByNameOrEmail("Marcin", "").get).isEqualTo(entityA)
      assertThat(storage.findByNameOrEmail("John", "").get).isEqualTo(entityB)
      assertThat(storage.findByNameOrEmail("Jesse", "").isEmpty).isTrue

      assertThat(storage.findByNameOrEmail("", "m.p@mp.pl").get).isEqualTo(entityA)
      assertThat(storage.findByNameOrEmail("", "j.s@mp.pl").get).isEqualTo(entityB)
      assertThat(storage.findByNameOrEmail("", "w.w@mp.pl").isEmpty).isTrue
    }
  }
}
