package pl.mpieciukiewicz.codereview.utils.clock

import org.joda.time.{Instant, DateTime}

/**
 *
 */
trait Clock {

  def now:DateTime

  def instantNow:Instant

}
