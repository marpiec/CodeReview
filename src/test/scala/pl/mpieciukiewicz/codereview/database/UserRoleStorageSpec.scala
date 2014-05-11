package pl.mpieciukiewicz.codereview.database

import org.scalatest.{BeforeAndAfter, GivenWhenThen, FeatureSpec}
import pl.mpieciukiewicz.codereview.database.engine.{DatabaseAccessor, DocumentDataStorage}
import pl.mpieciukiewicz.codereview.model.constant.ProjectRole
import collection.JavaConverters._
import org.fest.assertions.api.Assertions._
import pl.mpieciukiewicz.codereview.utils.json.JsonUtil
import pl.mpieciukiewicz.codereview.TestsUtil
import pl.mpieciukiewicz.codereview.TestsUtil._
import pl.mpieciukiewicz.codereview.model.persitent.UserRole
import pl.mpieciukiewicz.codereview.model.persitent.UserRole

/**
 *
 */
class UserRoleStorageSpec extends FeatureSpec with GivenWhenThen with BeforeAndAfter {

  var storage: UserRoleStorage = _

  before {
    storage = new UserRoleStorage(createTemporaryDataAccessor, new UniqueMemorySequenceManager)
  }

  after {
    storage.dba.close()
  }

  feature("Properly storing user data") {

    scenario("Storing and getting back correct users") {
      Given("Initialized data storage")

      When("Users roles are stored")
      val prototypeA = UserRole(0, 1, 2, ProjectRole.Admin)
      val prototypeB = UserRole(0, 2, 3, ProjectRole.Developer)
      val entityA = storage.add(prototypeA)
      val entityB = storage.add(prototypeB)

      Then("Data storage contains correct users in order they were stored")
      assertThat(storage.loadAll().asJava).containsExactly(entityA, entityB)

    }

    scenario("Getting back users for different perspectives") {
      Given("Initialized data storage with roles")
      val prototypeA = UserRole(0, 1, 2, ProjectRole.Admin)
      val prototypeB = UserRole(0, 1, 3, ProjectRole.Admin)
      val prototypeC = UserRole(0, 2, 3, ProjectRole.Developer)
      val entityA = storage.add(prototypeA)
      val entityB = storage.add(prototypeB)
      val entityC = storage.add(prototypeC)

      When("Getting all roles for project")
      assertThat(storage.findByProject(2).asJava).containsExactly(entityA)
      assertThat(storage.findByProject(3).asJava).containsExactly(entityB, entityC)

      When("Getting all roles for user")
      assertThat(storage.findByUser(1).asJava).containsExactly(entityA, entityB)
      assertThat(storage.findByUser(2).asJava).containsExactly(entityC)

      When("Getting user role in project")
      assertThat(storage.findByUserAndProject(1, 2).get).isEqualTo(entityA)
      assertThat(storage.findByUserAndProject(1, 3).get).isEqualTo(entityB)
      assertThat(storage.findByUserAndProject(2, 3).get).isEqualTo(entityC)
      assertThat(storage.findByUserAndProject(2, 1).isDefined).isFalse

    }
  }
}
