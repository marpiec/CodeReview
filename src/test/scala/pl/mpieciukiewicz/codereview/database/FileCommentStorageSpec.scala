package pl.mpieciukiewicz.codereview.database

import collection.JavaConverters._
import org.fest.assertions.api.Assertions._
import org.scalatest.{BeforeAndAfter, GivenWhenThen, FeatureSpec}
import pl.mpieciukiewicz.codereview.TestsUtil._
import org.joda.time.DateTime
import pl.mpieciukiewicz.codereview.model.FileComment
import pl.mpieciukiewicz.codereview.model.constant.BeforeOrAfter

class FileCommentStorageSpec extends FeatureSpec with GivenWhenThen with BeforeAndAfter {

  var storage:FileCommentStorage = _

  before {
    storage = new FileCommentStorage(createTemporaryDataAccessor, new UniqueMemorySequenceManager)
  }

  after {
    storage.dba.close()
  }

  feature("Storing commits' comments") {
    scenario("Storing and getting back correct comments") {

      Given("Initialized data storage")

      When("Comments are stored")

      val prototypeA = FileComment(1, 1, BeforeOrAfter.After, 1, DateTime.now(), "My First comment", None)
      val prototypeB = FileComment(1, 1, BeforeOrAfter.After, 1, DateTime.now(), "My second comment", None)
      val prototypeC = FileComment(2, 3, BeforeOrAfter.After, 2, DateTime.now(), "Someone else comment to other commit", None)

      val entityA = storage.add(prototypeA)
      val entityB = storage.add(prototypeB)
      val entityC = storage.add(prototypeC)

      val prototypeD = FileComment(1, 3, BeforeOrAfter.After, 2, DateTime.now(), "Someone else response to mine comment", Some(entityA.id))

      val entityD = storage.add(prototypeD)

      Then("Data storage contains correct commits in order they were stored")
      assertThat(storage.loadAll().asJava).containsExactly(entityA, entityB, entityC, entityD)

    }
  }


}
