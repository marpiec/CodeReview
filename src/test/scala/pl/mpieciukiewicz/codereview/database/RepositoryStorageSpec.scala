package pl.mpieciukiewicz.codereview.database

import org.scalatest.{BeforeAndAfter, GivenWhenThen, FeatureSpec}
import pl.mpieciukiewicz.codereview.model.Repository
import pl.mpieciukiewicz.codereview.database.engine.{DocumentDataStorage, DatabaseAccessor}
import org.joda.time.DateTime
import collection.JavaConverters._
import org.fest.assertions.api.Assertions._
import pl.mpieciukiewicz.codereview.utils.json.JsonUtil
import pl.mpieciukiewicz.codereview.TestsUtil

/**
 *
 */
class RepositoryStorageSpec extends FeatureSpec with GivenWhenThen with BeforeAndAfter {

  var storage: RepositoryStorage = _

  before {
    storage = new RepositoryStorage(new DocumentDataStorage(new DatabaseAccessor(TestsUtil.randomMemoryH2Url, "sa", "sa"), new JsonUtil))
    storage.dds.initDatabaseStructure()
  }

  after {
    storage.dds.close()
  }

  feature("Properly storing repository data") {

    scenario("Storing and getting back correct repositories") {
      Given("Initialized data storage")

      When("Repositories are stored")
      val prototypeA = Repository(None, 1, "version 1 repository", "https://github.com/marpiec/mpjsons.git", "tmp/mpjsons", DateTime.now())
      val prototypeB = Repository(None, 2, "main repository", "https://github.com/marpiec/socnet.git", "tmp/socnet", DateTime.now())
      val entityA = storage.add(prototypeA)
      val entityB = storage.add(prototypeB)

      Then("Data storage contains correct users in order they were stored")
      assertThat(storage.loadAll().asJava).containsExactly(entityA, entityB)

    }

    scenario("Storing and getting back correct repositories by project") {

      Given("Initialized data storage")

      When("Repositories are stored")
      val prototypeA = Repository(None, 1, "version 1 repository", "https://github.com/marpiec/mpjsons.git", "tmp/mpjsons", DateTime.now())
      val prototypeB = Repository(None, 2, "github repository", "https://github.com/marpiec/socnet.git", "tmp/socnet", DateTime.now())
      val prototypeC = Repository(None, 1, "version 1  repository", "https://github.com/marpiec/mpjsons2.git", "tmp/mpjsons2", DateTime.now())
      val entityA = storage.add(prototypeA)
      val entityB = storage.add(prototypeB)
      val entityC = storage.add(prototypeC)

      Then("Can be loaded by project")
      assertThat(storage.findByProject(1).asJava).containsExactly(entityA, entityC)
      assertThat(storage.findByProject(2).asJava).containsExactly(entityB)

    }

    scenario("Storing and getting back correct repositories by id") {
      Given("Initialized data storage")

      When("Repositories are stored")
      val prototypeA = Repository(None, 1, "github repository", "https://github.com/marpiec/mpjsons.git", "tmp/mpjsons", DateTime.now())
      val prototypeB = Repository(None, 2, "github repository", "https://github.com/marpiec/socnet.git", "tmp/socnet", DateTime.now())
      val entityA = storage.add(prototypeA)
      val entityB = storage.add(prototypeB)

      Then("Repositories can be found by name")
      assertThat(storage.findById(entityA.id.get).get).isEqualTo(entityA)
      assertThat(storage.findById(entityB.id.get).get).isEqualTo(entityB)
      assertThat(storage.findById(entityA.id.get + entityB.id.get + 1).isDefined).isFalse

    }
  }
}
