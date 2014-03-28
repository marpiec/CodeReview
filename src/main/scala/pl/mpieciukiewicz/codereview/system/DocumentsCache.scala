package pl.mpieciukiewicz.codereview.system

import org.joda.time.Instant
import scala.concurrent.Future

/**
 *
 */
class DocumentsCache {

  var cache: Map[String, Future[String]] = Map()
  var touchTime: Map[String, Instant] = Map()

  def getOrInsert(key: String)(block: => Future[String]):Future[String] = synchronized {
    touchTime += key -> Instant.now()
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
