package pl.mpieciukiewicz.codereview.database

import collection.JavaConverters._
import org.fest.assertions.api.Assertions._
import org.scalatest.{BeforeAndAfter, GivenWhenThen, FeatureSpec}
import pl.mpieciukiewicz.codereview.TestsUtil._
import org.joda.time.DateTime
import pl.mpieciukiewicz.codereview.model.{LineComment, FileComment}
import pl.mpieciukiewicz.codereview.model.constant.BeforeOrAfter
import pl.mpieciukiewicz.codereview.model.LineComment
import pl.mpieciukiewicz.codereview.model.FileComment
import scala.Some

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

  feature("Comments can be searched") {

    scenario("All comments for the given commit can be found") {

      Given("Initialized data storage with comments stored")

      val prototypeA = FileComment(1, 1, BeforeOrAfter.After, 1, DateTime.now(), "My First comment", None)
      val prototypeB = FileComment(1, 1, BeforeOrAfter.After, 1, DateTime.now(), "My second comment", None)
      val prototypeC = FileComment(2, 3, BeforeOrAfter.After, 2, DateTime.now(), "Someone else comment to other commit", None)

      val entityA = storage.add(prototypeA)
      val entityB = storage.add(prototypeB)
      val entityC = storage.add(prototypeC)

      When("Comments are searched by commit id")

      val commit1Comments = storage.findByCommitId(1)
      val commit2Comments = storage.findByCommitId(2)

      Then("All comments for given commit id are found")


      assertThat(commit1Comments.asJava).containsExactly(entityA, entityB)
      assertThat(commit2Comments.asJava).containsExactly(entityC)


    }

  }

}
