package pl.mpieciukiewicz.codereview.database

import pl.mpieciukiewicz.codereview.database.engine.DatabaseAccessor

class DatabaseSequenceManager(val dba: DatabaseAccessor) extends SequenceManager {

  final val BUFFER_SIZE = 20

  private var userId = 0

  def nextUserId():Int = synchronized {
    if (userId % BUFFER_SIZE == 0) {
      userId= loadNext("user_seq")
    }
    userId += 1
    userId
  }

  private def loadNext(sequenceName: String): Int = {
    dba.selectNoParams("SELECT NEXTVAL('" + sequenceName + "')") {
      resultSet =>
        resultSet.next()
        resultSet.getInt(1)
    }
  }


}
