package pl.mpieciukiewicz.codereview.utils

import org.scalatest.{GivenWhenThen, FeatureSpec}
import org.fest.assertions.api.Assertions._
import pl.mpieciukiewicz.codereview.utils.protectedid.IdProtectionUtil

class IdProtectionUtilSpec extends FeatureSpec with GivenWhenThen {

  feature("Can encrypt identifier") {

    scenario("Encrypts and decrypts identifer") {

      Given("Some identifer")
      val id = UID(12345)

      When("Identifier is encrypted")
      val encrypted:String = IdProtectionUtil.encrypt(id)

      Then("Identifier is encrypted")
      assertThat(encrypted).isEqualTo("u394ah598pej")

      When("Identifier is decrypted")
      val decrypted:UID = IdProtectionUtil.decrypt(encrypted)

      Then("Identifier is correct")
      assertThat(decrypted).isEqualTo(id)
    }

  }

}
