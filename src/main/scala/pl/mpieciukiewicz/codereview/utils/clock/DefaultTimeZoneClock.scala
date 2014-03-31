package pl.mpieciukiewicz.codereview.utils.clock

import org.joda.time.{Instant, DateTime}

/**
 *
 */
class DefaultTimeZoneClock extends Clock {
  override def instantNow: Instant = new Instant()

  override def now: DateTime = new DateTime()
}
