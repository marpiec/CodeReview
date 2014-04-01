package pl.mpieciukiewicz.codereview.database

import org.scalatest.{BeforeAndAfter, GivenWhenThen, FeatureSpec}
import collection.JavaConverters._
import org.fest.assertions.api.Assertions._
import pl.mpieciukiewicz.codereview.model.Project
import pl.mpieciukiewicz.codereview.database.engine.{DatabaseAccessor, DocumentDataStorage}
import pl.mpieciukiewicz.codereview.utils.json.JsonUtil
import pl.mpieciukiewicz.codereview.TestsUtil

/**
 *
 */
class ProjectStorageSpec extends FeatureSpec with GivenWhenThen with BeforeAndAfter {

  var storage:ProjectStorage = _

  before {
    storage = new ProjectStorage(new DocumentDataStorage(new DatabaseAccessor(TestsUtil.randomMemoryH2Url, "sa", "sa"), new JsonUtil))
    storage.dds.initDatabaseStructure()
  }

  after {
    storage.dds.close()
  }

  feature("Properly storing project data") {

    scenario("Storing and getting back correct projects") {
      Given("Initialized data storage")

      When("Projects are stored")
      val prototypeA = Project(None, "mpjsons")
      val prototypeB = Project(None, "CodeReview")
      val entityA = storage.add(prototypeA)
      val entityB = storage.add(prototypeB)

      Then("Data storage contains correct projects in order they were stored")
      assertThat(storage.loadAll().asJava).containsExactly(entityA, entityB)

    }

    scenario("Storing and getting back correct projects by id") {
      Given("Initialized data storage")

      When("Projects are stored")
      val prototypeA = Project(None, "mpjsons")
      val prototypeB = Project(None, "CodeReview")
      val entityA = storage.add(prototypeA)
      val entityB = storage.add(prototypeB)

      Then("Projects can be found by name")
      assertThat(storage.findById(entityA.id.get).get).isEqualTo(entityA)
      assertThat(storage.findById(entityB.id.get).get).isEqualTo(entityB)
      assertThat(storage.findById(entityA.id.get + entityB.id.get + 1).isDefined).isFalse

    }
  }
}
