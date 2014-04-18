package pl.mpieciukiewicz.codereview.database

import org.scalatest.{BeforeAndAfter, GivenWhenThen, FeatureSpec}
import collection.JavaConverters._
import org.fest.assertions.api.Assertions._
import pl.mpieciukiewicz.codereview.model.{Commit, Project}
import pl.mpieciukiewicz.codereview.database.engine.{DatabaseAccessor, DocumentDataStorage}
import pl.mpieciukiewicz.codereview.utils.json.JsonUtil
import org.joda.time.DateTime
import pl.mpieciukiewicz.codereview.TestsUtil._
import pl.mpieciukiewicz.codereview.model.Commit

/**
 *
 */
class CommitStorageSpec extends FeatureSpec with GivenWhenThen with BeforeAndAfter {

  var storage:CommitStorage = _

  before {
    storage = new CommitStorage(createTemporaryDataStorage)
  }

  after {
    storage.dds.close()
  }

  feature("Properly storing commits data") {

    scenario("Storing and getting back correct commits") {
      Given("Initialized data storage")

      When("Commits are stored")
      val prototypeA = Commit(None, 1, DateTime.now(), "asdr23r", "Marcin", "Marcin", "first commit", "master")
      val prototypeB = Commit(None, 2, DateTime.now(), "46fddh", "Marcin", "John", "second commit", "master")
      val entityA = storage.add(prototypeA)
      val entityB = storage.add(prototypeB)

      Then("Data storage contains correct commits in order they were stored")
      assertThat(storage.loadAll().asJava).containsExactly(entityA, entityB)

    }

    scenario("Storing all at once and getting back correct commits by id") {
      Given("Initialized data storage")

      When("Projects are stored at once")
      val prototypeA = Commit(None, 1, DateTime.now(), "asdr23r", "Marcin", "Marcin", "first commit", "master")
      val prototypeB = Commit(None, 2, DateTime.now(), "46fddh", "Marcin", "John", "second commit", "master")
      val entities:List[Commit] = storage.addAll(List(prototypeA, prototypeB))

      val entityA = entities(0)
      val entityB = entities(1)

      Then("Projects can be found by name")
      assertThat(storage.findById(entityA.id.get).get).isEqualTo(entityA)
      assertThat(storage.findById(entityB.id.get).get).isEqualTo(entityB)
      assertThat(storage.findById(entityA.id.get + entityB.id.get + 1).isDefined).isFalse

    }

    scenario("Storing and getting back correct commits by repository id") {
      Given("Initialized data storage")

      When("Projects are stored")
      val prototypeA = Commit(None, 1, DateTime.now(), "asdr23r", "Marcin", "Marcin", "first commit", "master")
      val prototypeB = Commit(None, 2, DateTime.now(), "46fddh", "Marcin", "John", "second commit", "master")
      val entityA = storage.add(prototypeA)
      val entityB = storage.add(prototypeB)

      Then("Projects can be found by name")
      assertThat(storage.findByRepositoryId(1).asJava).containsExactly(entityA)
      assertThat(storage.findByRepositoryId(2).asJava).containsExactly(entityB)
      assertThat(storage.findByRepositoryId(3).asJava).isEmpty()
    }
  }
}
