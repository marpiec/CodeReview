package pl.mpieciukiewicz.codereview.system

import org.joda.time.Instant
import scala.concurrent.Future
import pl.mpieciukiewicz.codereview.utils.clock.Clock

/**
 *
 */
class DocumentsCache {

  var cache: Map[String, Future[String]] = Map()

  def getOrInsert(key: String)(block: => Future[String]):Future[String] = synchronized {
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
