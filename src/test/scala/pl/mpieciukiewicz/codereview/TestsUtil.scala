package pl.mpieciukiewicz.codereview

import pl.mpieciukiewicz.codereview.database.engine.{DatabaseAccessor, DocumentDataStorage}
import pl.mpieciukiewicz.codereview.utils.json.JsonUtil
import java.util.concurrent.atomic.AtomicInteger

object TestsUtil {

  val h2DatabaseNameCounter = new AtomicInteger(0)

  def uniqueMemoryH2Url: String = {
    "jdbc:h2:mem:" + h2DatabaseNameCounter.getAndIncrement
  }

  def createTemporaryDataStorage = {
    val storage = new DocumentDataStorage(new DatabaseAccessor(uniqueMemoryH2Url, "sa", "sa"), new JsonUtil)
    storage.initDatabaseStructure()
    storage
  }

}
