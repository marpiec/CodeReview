package pl.mpieciukiewicz.codereview

import pl.mpieciukiewicz.codereview.database.engine.{DatabaseAccessor, DocumentDataStorage}
import pl.mpieciukiewicz.codereview.utils.json.JsonUtil
import java.util.concurrent.atomic.AtomicInteger
import pl.mpieciukiewicz.codereview.database.SequenceManager

object TestsUtil {

  val h2DatabaseNameCounter = new AtomicInteger(0)

  def uniqueMemoryH2Url: String = {
    "jdbc:h2:mem:" + h2DatabaseNameCounter.getAndIncrement
  }


  def createTemporaryDataStorage = {
    val storage = new DocumentDataStorage(createTemporaryDataAccessor, new JsonUtil)
    storage.initDatabaseStructure()
    storage
  }

  def createTemporaryDataAccessor = {
    new DatabaseAccessor(uniqueMemoryH2Url, "sa", "sa")
  }


  class UniqueMemorySequenceManager extends SequenceManager {
    private val id = new AtomicInteger(0)
    override def nextId(sequenceName: String): Int = id.incrementAndGet()
  }

}
