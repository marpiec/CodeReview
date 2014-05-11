package pl.mpieciukiewicz.codereview.utils

import org.scalatest.{GivenWhenThen, FeatureSpec}
import org.fest.assertions.api.Assertions._
import pl.mpieciukiewicz.codereview.utils.json.JsonUtil
import pl.mpieciukiewicz.codereview.model.constant.SystemRole
import pl.mpieciukiewicz.codereview.model.persitent.User

/**
 *
 */
class JsonUtilSpec extends FeatureSpec with GivenWhenThen {

  val jsonUtil = new JsonUtil
  val user = User(Some(12), "Marcin", "m.p@mp.pl", "sdgserg", "abc", SystemRole.Admin)
  val userJson = """{"id":[12],"name":"Marcin","email":"m.p@mp.pl","passwordHash":"sdgserg","salt":"abc","systemRole":"Admin"}"""

  feature("Should serialize to and deserialize from Json correctly") {

    scenario("Should serialize to Json correctly") {
      Given("JSON util and user object")
      When("User object is serialized")
      val json = jsonUtil.toJson(user)

      Then("It should be represented correctly as JSON String")
      assertThat(json).isEqualTo(userJson)
    }


    scenario("Should deserialize to Json correctly") {
      Given("JSON util and user object as json string")
      When("User json is deserialized")
      val deserializedUser = jsonUtil.fromJson(userJson, classOf[User])

      Then("It should contain proper values")
      assertThat(deserializedUser).isEqualTo(user)
    }
  }

}
