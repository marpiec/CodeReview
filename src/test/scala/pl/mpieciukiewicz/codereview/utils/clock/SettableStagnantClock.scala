package pl.mpieciukiewicz.codereview.utils.clock

import org.joda.time.{Instant, DateTime}

class SettableStagnantClock extends Clock {

  var time: DateTime = new DateTime
  
  override def now: DateTime = time

  override def instantNow: Instant = time.toInstant

  def setHour(hour: Int) = {
    time = time.withHourOfDay(hour)
    this
  }

  def setMinute(minute: Int) = {
    time = time.withMinuteOfHour(minute)
    this
  }

  def setSecond(second: Int) = {
    time = time.withSecondOfMinute(second)
    this
  }

  def plusHours(hours: Int) = {
    time = time.plusHours(hours)
    this
  }

  def plusMinutes(minutes: Int) = {
    time = time.plusMinutes(minutes)
    this
  }

  def plusSeconds(seconds: Int) = {
    time = time.plusSeconds(seconds)
    this
  }

}
