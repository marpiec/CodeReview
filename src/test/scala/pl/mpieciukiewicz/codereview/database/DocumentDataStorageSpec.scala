package pl.mpieciukiewicz.codereview.database

import org.scalatest.{BeforeAndAfter, GivenWhenThen, FeatureSpec, FunSuite}
import collection.JavaConverters._
import org.fest.assertions.api.Assertions._
import pl.mpieciukiewicz.codereview.model.User
import pl.mpieciukiewicz.codereview.utils.{DatabaseAccessor, JsonUtil}

/**
 *
 */
class DocumentDataStorageSpec extends FeatureSpec with GivenWhenThen with BeforeAndAfter {

  var ds:DocumentDataStorage = _

  before {
    ds = new DocumentDataStorage(new DatabaseAccessor("jdbc:h2:mem:testdb", "sa", "sa"), new JsonUtil)
    ds.initDatabaseStructure()
  }

  after {
    ds.close()
  }

  feature("Properly storing data") {

    scenario("Creating database structure") {

      Given("Initialized data storage")

      Then("Contains no users in entity table")
      ds.dba.selectNoParams("SELECT * FROM entity") { resultSet =>
        assertThat(resultSet.next).isFalse
      }

      Then("Contains initialized user sequence")
      ds.dba.selectNoParams("SELECT NEXTVAL('entity_seq')") { resultSet =>
        assertThat(resultSet.next()).isTrue
        assertThat(resultSet.getInt(1)).isEqualTo(1)
        assertThat(resultSet.next()).isFalse
      }

    }



  }

}
