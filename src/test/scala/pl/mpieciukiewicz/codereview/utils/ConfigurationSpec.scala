package pl.mpieciukiewicz.codereview.utils

import org.scalatest.{GivenWhenThen, FeatureSpec}
import org.fest.assertions.api.Assertions._

class ConfigurationSpec extends FeatureSpec with GivenWhenThen {

  feature("Provides proper configuration") {

    scenario("Loads proper default project configuration") {

      Given("Configuration")
      val config = Configuration.fromClasspath("/application.conf")

      Then("Configuration contains proper options")

      assertThat(config.webServer.port).isEqualTo(8080)

      assertThat(config.proxy.enabled).isFalse
      assertThat(config.proxy.host).isEqualTo("some.proxy.com")
      assertThat(config.proxy.port).isEqualTo(8080)

      assertThat(config.storage.dataDirectory).isEqualTo("../data/")

      assertThat(config.security.systemSalt).isEqualTo("SecureSalt")

      Then("String representation is not empty")

      assertThat(config.toString).isNotEmpty

    }

  }

}
