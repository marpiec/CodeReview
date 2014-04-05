package pl.mpieciukiewicz.codereview.utils.clock

import org.scalatest._
import org.fest.assertions.api.Assertions._

class SettableStagnantClockSpec extends FeatureSpecLike with GivenWhenThen {

  feature("Settable clock with default time as system time at the point of clock creation") {

    scenario("Returns time of creation if not modified") {

      Given("New clock instance")
      val before = System.currentTimeMillis()
      val clock = new SettableStagnantClock
      val after = System.currentTimeMillis()

      When("DateTime and instant are taken")
      val dateTime = clock.now
      val instant = clock.now

      Then("Date time and instant points to the same point in time")
      assertThat(dateTime.toDate).isEqualTo(instant.toDate)

      Then("DateTime points to the time when clock was created")

      assertThat(dateTime.toDate.getTime).isGreaterThanOrEqualTo(before)
      assertThat(dateTime.toDate.getTime).isLessThanOrEqualTo(after)

    }

    scenario("Time of the clock is modifiable") {
      Given("New clock instance")
      var clock = new SettableStagnantClock

      When("Hour, minute and second is set, and dateTime taken")
      clock = clock.setHour(15).setMinute(10).setSecond(32)
      var now = clock.now

      Then("Time taken from the clock has proper hour, minute and second set")
      assertThat(now.getHourOfDay).isEqualTo(15)
      assertThat(now.getMinuteOfHour).isEqualTo(10)
      assertThat(now.getSecondOfMinute).isEqualTo(32)

      When("Hours, minutes and seconds are added to the clock")
      clock = clock.plusHours(2).plusMinutes(3).plusSeconds(1)
      now = clock.now

      Then("Time taken from the clock has proper hour, minute and second set")
      assertThat(now.getHourOfDay).isEqualTo(17)
      assertThat(now.getMinuteOfHour).isEqualTo(13)
      assertThat(now.getSecondOfMinute).isEqualTo(33)

    }
  }


}
