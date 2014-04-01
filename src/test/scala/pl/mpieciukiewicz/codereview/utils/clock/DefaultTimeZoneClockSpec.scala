package pl.mpieciukiewicz.codereview.utils.clock

import org.scalatest._
import org.fest.assertions.api.Assertions._

class DefaultTimeZoneClockSpec extends FeatureSpecLike with GivenWhenThen {

  feature("Returns current system time") {

    scenario("Returns proper DateTime instance with current system time") {

      Given("Clock instance")
      val clock = new DefaultTimeZoneClock

      When("Timestamps are taken")
      val before = System.currentTimeMillis()
      val now = clock.now
      val after = System.currentTimeMillis()

      Then("Timestamps have proper order")

      assertThat(now.toDate.getTime).isGreaterThanOrEqualTo(before)
      assertThat(now.toDate.getTime).isLessThanOrEqualTo(after)

    }

    scenario("Returns proper Instant instance with current system time") {
      Given("Clock instance")
      val clock = new DefaultTimeZoneClock

      When("Timestamps are taken")
      val before = System.currentTimeMillis()
      val now = clock.instantNow
      val after = System.currentTimeMillis()

      Then("Timestamps have proper order")

      assertThat(now.getMillis).isGreaterThanOrEqualTo(before)
      assertThat(now.getMillis).isLessThanOrEqualTo(after)
    }


  }

}
