package pl.mpieciukiewicz.codereview.database

import pl.mpieciukiewicz.codereview.database.engine.DatabaseAccessor
import java.util.concurrent.atomic.AtomicInteger

class DatabaseSequenceManager(val dba: DatabaseAccessor) extends SequenceManager {

  final val BUFFER_SIZE = 20

  private var ids: Map[String, AtomicInteger] = Map()

  def nextId(sequenceName: String):Int = synchronized {
    if(!ids.contains(sequenceName)) {
      ids += sequenceName -> new AtomicInteger(0)
    }
    if (ids(sequenceName).get() % BUFFER_SIZE == 0) {
      val id = loadNext(sequenceName)
      ids(sequenceName).set(id)
      id
    } else {
      ids(sequenceName).incrementAndGet()
    }
  }

  private def loadNext(sequenceName: String): Int = {
    dba.selectNoParams("SELECT NEXTVAL('" + sequenceName + "')") {
      resultSet =>
        resultSet.next()
        resultSet.getInt(1)
    }
  }


}
