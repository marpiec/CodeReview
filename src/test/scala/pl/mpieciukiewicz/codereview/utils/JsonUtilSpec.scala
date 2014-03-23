package pl.mpieciukiewicz.codereview.utils

import org.scalatest.{GivenWhenThen, FeatureSpec}
import org.fest.assertions.api.Assertions._
import pl.mpieciukiewicz.codereview.model.User
import pl.mpieciukiewicz.codereview.utils.json.JsonUtil

/**
 *
 */
class JsonUtilSpec extends FeatureSpec with GivenWhenThen {

  val jsonUtil = new JsonUtil
  val user = User(Some(12), "Marcin", "m.p@mp.pl", "pass123", "abc")
  val userJson = """{"id":[12], "name":"Marcin","password":"pass123","salt":"abc","email":"m.p@mp.pl"}"""

  feature("Should serialize to and deserialize from Json correctly") {

    scenario("Should serialize to Json correctly") {
      Given("JSON util and user object")
      When("User object is serialized")
      val userJson = jsonUtil.toJson(user)

      Then("It should be represented correctly as JSON String")
      assertThat(userJson).isEqualTo(userJson)
    }


    scenario("Should deserialize to Json correctly") {
      Given("JSON util and user object as json string")
      When("User json is deserialized")
      val user = jsonUtil.fromJson(userJson, classOf[User])

      Then("It should contain proper values")
      assertThat(user).isEqualTo(user)
    }
  }

}
