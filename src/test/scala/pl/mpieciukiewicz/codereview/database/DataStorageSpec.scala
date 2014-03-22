package pl.mpieciukiewicz.codereview.database

import org.scalatest.{BeforeAndAfter, GivenWhenThen, FeatureSpec, FunSuite}
import collection.JavaConverters._
import org.fest.assertions.api.Assertions._
import pl.mpieciukiewicz.codereview.model.User

/**
 *
 */
class DataStorageSpec extends FeatureSpec with GivenWhenThen with BeforeAndAfter {

  var ds:DataStorage = _

  before {
    ds = new DataStorage("jdbc:h2:mem:testdb", "sa", "sa")
    ds.initDatabaseStructure()
  }

  after {
    ds.close()
  }

  feature("Properly storing data") {

    scenario("Creating database structure") {

      Given("Initialized data storage")

      Then("Contains empty user table")
      assertThat(ds.loadAllUsers().asJava).isEmpty()

      Then("Contains initialized user sequence")
      ds.dba.selectNoParams("SELECT NEXTVAL('user_seq')") { resultSet =>
        assertThat(resultSet.next()).isTrue
        assertThat(resultSet.getInt(1)).isEqualTo(1)
        assertThat(resultSet.next()).isFalse
      }

    }


    scenario("Storing and getting back correct users") {
      Given("Initialized data storage")

      When("Users are stored")
      val userA = User("Marcin", "pass123", "abc", "m.p@mp.pl", "admin")
      val userB = User("John", "321pass", "123", "j.s@mp.pl", "developer")
      ds.addUser(userA)
      ds.addUser(userB)

      Then("Data storage contains correct users in order they were stored")
      assertThat(ds.loadAllUsers().asJava).containsExactly(userA, userB)

    }

    scenario("Storing and getting back correct users by name") {
      Given("Initialized data storage")

      When("Users are stored")
      val userA = User("Marcin", "pass123", "abc", "m.p@mp.pl", "admin")
      val userB = User("John", "321pass", "123", "j.s@mp.pl", "developer")
      ds.addUser(userA)
      ds.addUser(userB)

      Then("Users can be found by name")
      assertThat(ds.findUserByName("Marcin").get).isEqualTo(userA)
      assertThat(ds.findUserByName("John").get).isEqualTo(userB)
      assertThat(ds.findUserByName("Jesse").isEmpty).isTrue

    }

  }

}
