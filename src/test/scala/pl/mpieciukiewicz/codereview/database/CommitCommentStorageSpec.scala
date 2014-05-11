package pl.mpieciukiewicz.codereview.database

import collection.JavaConverters._
import org.fest.assertions.api.Assertions._
import org.scalatest.{BeforeAndAfter, GivenWhenThen, FeatureSpec}
import pl.mpieciukiewicz.codereview.TestsUtil._
import org.joda.time.DateTime
import pl.mpieciukiewicz.codereview.model.CommitComment

class CommitCommentStorageSpec extends FeatureSpec with GivenWhenThen with BeforeAndAfter {

  var storage:CommitCommentStorage = _

  before {
    storage = new CommitCommentStorage(createTemporaryDataAccessor, new UniqueMemorySequenceManager)
  }

  after {
    storage.dba.close()
  }

  feature("Storing commits' comments") {
    scenario("Storing and getting back correct comments") {

      Given("Initialized data storage")

      When("Comments are stored")

      val prototypeA = CommitComment(1, 1, DateTime.now(), "My First comment", None)
      val prototypeB = CommitComment(1, 1, DateTime.now(), "My second comment", None)
      val prototypeC = CommitComment(2, 3, DateTime.now(), "Someone else comment to other commit", None)

      val entityA = storage.add(prototypeA)
      val entityB = storage.add(prototypeB)
      val entityC = storage.add(prototypeC)

      val prototypeD = CommitComment(1, 3, DateTime.now(), "Someone else response to mine comment", Some(entityA.id))

      val entityD = storage.add(prototypeD)

      Then("Data storage contains correct commits in order they were stored")
      assertThat(storage.loadAll().asJava).containsExactly(entityA, entityB, entityC, entityD)

    }
  }


}
