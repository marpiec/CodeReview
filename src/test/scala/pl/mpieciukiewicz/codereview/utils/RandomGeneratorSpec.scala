package pl.mpieciukiewicz.codereview.utils

import org.scalatest._
import org.fest.assertions.api.Assertions._

class RandomGeneratorSpec extends FeatureSpecLike with GivenWhenThen {

  final val ALPHANUMERIC_CHECK_REGEXP = "[a-zA-Z0-9]+"

  feature("Generates random alphanumeric Strings") {
    
    scenario("Generates proper session identifiers") {
      Given("Random generator")
      val generator = new RandomGenerator

      When("Multiple session ids are generated")

      val a = generator.generateSessionIdentifier
      val b = generator.generateSessionIdentifier
      val c = generator.generateSessionIdentifier

      Then("Identifier are most probably different")
      assertThat(a).isNotEqualTo(b).isNotEqualTo(c)
      assertThat(b).isNotEqualTo(c)

      Then("They have the same, proper length")
      assertThat(a.length).isEqualTo(b.length).isEqualTo(c.length).isEqualTo(32)

      Then("They contains only alphanumeric characters")

      assertThat(a).matches(ALPHANUMERIC_CHECK_REGEXP)
      assertThat(b).matches(ALPHANUMERIC_CHECK_REGEXP)
      assertThat(c).matches(ALPHANUMERIC_CHECK_REGEXP)
    }

    scenario("Generates proper random directory name") {
      Given("Random generator")
      val generator = new RandomGenerator

      When("Multiple directories names are generated")

      val a = generator.generateRepoDirectoryName
      val b = generator.generateRepoDirectoryName
      val c = generator.generateRepoDirectoryName

      Then("Names are most probably different")
      assertThat(a).isNotEqualTo(b).isNotEqualTo(c)
      assertThat(b).isNotEqualTo(c)

      Then("They have the same, proper length")
      assertThat(a.length).isEqualTo(b.length).isEqualTo(c.length).isEqualTo(8)

      Then("They contains only alphanumeric characters")

      assertThat(a).matches(ALPHANUMERIC_CHECK_REGEXP)
      assertThat(b).matches(ALPHANUMERIC_CHECK_REGEXP)
      assertThat(c).matches(ALPHANUMERIC_CHECK_REGEXP)
    }
  }
  
  
}
