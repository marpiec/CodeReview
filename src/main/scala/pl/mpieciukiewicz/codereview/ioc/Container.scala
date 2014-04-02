package pl.mpieciukiewicz.codereview.ioc

import pl.mpieciukiewicz.codereview.database._
import pl.mpieciukiewicz.codereview.database.engine.{DatabaseAccessor, DocumentDataStorage}
import pl.mpieciukiewicz.codereview.utils.json.JsonUtil
import pl.mpieciukiewicz.codereview.system.DocumentsCache
import pl.mpieciukiewicz.codereview.utils.RandomGenerator
import pl.mpieciukiewicz.codereview.utils.Configuration
import com.typesafe.config.ConfigFactory
import java.io.InputStreamReader
import pl.mpieciukiewicz.codereview.utils.clock.DefaultTimeZoneClock

/**
 *
 */
class Container {

  private val MAIN_CONFIG_FILE = "/application.conf"
  val configuration = Configuration.fromClasspath(MAIN_CONFIG_FILE)

  val clock = new DefaultTimeZoneClock
  val jsonUtil = new JsonUtil
  val randomUtil = new RandomGenerator

  val databaseAccessor = new DatabaseAccessor("jdbc:h2:"+configuration.storage.dataDirectory+"/codeReview", "sa", "sa")
  val documentDataStorage = new DocumentDataStorage(databaseAccessor, jsonUtil)
  documentDataStorage.initDatabaseStructure()

  val userStorage = new UserStorage(documentDataStorage)
  val projectStorage = new ProjectStorage(documentDataStorage)
  val userRoleStorage = new UserRoleStorage(documentDataStorage)
  val repositoryStorage = new RepositoryStorage(documentDataStorage)
  val commitStorage = new CommitStorage(documentDataStorage)

  val documentsCache = new DocumentsCache(clock)

}

object Container {
  var instance = new Container
}
