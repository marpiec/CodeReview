package pl.mpieciukiewicz.codereview.utils.clock

import org.joda.time.{Instant, DateTime}

class SettableStagnantClock(time: DateTime = new DateTime) extends Clock {

  override def now: DateTime = time

  override def instantNow: Instant = time.toInstant

  def setHour(hour: Int) = {
    new SettableStagnantClock(time.withHourOfDay(hour))
  }

  def setMinute(minute: Int) = {
    new SettableStagnantClock(time.withMinuteOfHour(minute))
  }

  def setSecond(second: Int) = {
    new SettableStagnantClock(time.withSecondOfMinute(second))
  }

  def plusHours(hours: Int) = {
    new SettableStagnantClock(time.plusHours(hours))
  }

  def plusMinutes(minutes: Int) = {
    new SettableStagnantClock(time.plusMinutes(minutes))
  }

  def plusSeconds(seconds: Int) = {
    new SettableStagnantClock(time.plusSeconds(seconds))
  }

}
