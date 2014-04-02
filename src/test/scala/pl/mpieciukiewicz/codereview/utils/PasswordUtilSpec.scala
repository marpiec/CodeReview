package pl.mpieciukiewicz.codereview.utils

import org.scalatest.{GivenWhenThen, FeatureSpec}
import org.fest.assertions.api.Assertions._

class PasswordUtilSpec extends FeatureSpec with GivenWhenThen {

  final val ALPHANUMERIC_CHECK_REGEXP = "[a-zA-Z0-9]+"

  feature("Includes system provided salt to computed password hash") {

    scenario("Generate long enough hash and finishes within given time") {
      Given("Password util with system salt, password and salt")
      val util = new PasswordUtil("systemSalt")
      val password = "VerySecretPassword"
      val salt = "someRandomSalt"

      When("Generating password hash")
      val startTime = System.currentTimeMillis()
      val hash = util.hashPassword(password, salt)
      val endTime = System.currentTimeMillis()

      Then("Hash has correct length")
      assertThat(hash).hasSize(128)

      Then("Computation finished within given time")
      assertThat(endTime - startTime).isLessThan(500)
    }

    scenario("Two different system salts gives two different hash for same passwords and salts") {
      Given("Two password utils with different salts, password and salt")
      val utilA = new PasswordUtil("systemSaltA")
      val utilB = new PasswordUtil("systemSaltB")
      val password = "VerySecretPassword"
      val salt = "someRandomSalt"

      When("Password hashes are computed")
      val hashA = utilA.hashPassword(password, salt)
      val hashB = utilB.hashPassword(password, salt)

      Then("Generated hashes are different")
      assertThat(hashA).isNotEqualTo(hashB)
    }


    scenario("Two different salts gives two different hash for same passwords and system salts") {
      Given("Password util with defined system salt, password and two different salts")
      val util = new PasswordUtil("systemSalt")
      val password = "VerySecretPassword"
      val saltA = "saltA"
      val saltB = "saltB"

      When("Password hashes are computed")
      val hashA = util.hashPassword(password, saltA)
      val hashB = util.hashPassword(password, saltB)

      Then("Generated hashes are different")
      assertThat(hashA).isNotEqualTo(hashB)
    }

    scenario("Two different passwords gives two different hash for same salt and system salts") {
      Given("Password util with defined system salt, two different passwords and salt")
      val util = new PasswordUtil("systemSalt")
      val passwordA = "VerySecretPasswordA"
      val passwordB = "VerySecretPasswordB"
      val salt = "salt"

      When("Password hashes are computed")
      val hashA = util.hashPassword(passwordA, salt)
      val hashB = util.hashPassword(passwordB, salt)

      Then("Generated hashes are different")
      assertThat(hashA).isNotEqualTo(hashB)
    }

    scenario("Generates proper random salt") {
      Given("Random generator")
      val generator = new PasswordUtil("systemSalt")

      When("Multiple salts are generated")

      val a = generator.generateRandomSalt
      val b = generator.generateRandomSalt
      val c = generator.generateRandomSalt

      Then("Salts are most probably different")
      assertThat(a).isNotEqualTo(b).isNotEqualTo(c)
      assertThat(b).isNotEqualTo(c)

      Then("They have the same, proper length")
      assertThat(a.length).isEqualTo(b.length).isEqualTo(c.length).isEqualTo(24)

      Then("They contains only alphanumeric characters")

      assertThat(a).matches(ALPHANUMERIC_CHECK_REGEXP)
      assertThat(b).matches(ALPHANUMERIC_CHECK_REGEXP)
      assertThat(c).matches(ALPHANUMERIC_CHECK_REGEXP)
    }

  }

}
