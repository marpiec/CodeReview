package pl.mpieciukiewicz.codereview.system

import org.joda.time.Instant
import scala.concurrent.Future
import pl.mpieciukiewicz.codereview.utils.clock.Clock

/**
 *
 */
class DocumentsCache(clock: Clock) {

  var cache: Map[String, Future[String]] = Map()
  var touchTime: Map[String, Instant] = Map()

  def getOrInsert(key: String)(block: => Future[String]):Future[String] = synchronized {
    touchTime += key -> clock.instantNow
    val cached = cache.get(key)
    if(cached.isDefined) {
      cached.get
    } else {
      val calculated = block
      cache += key -> calculated
      calculated
    }
  }



}
