package pl.mpieciukiewicz.codereview.ioc

import pl.mpieciukiewicz.codereview.database._
import pl.mpieciukiewicz.codereview.database.engine.{DatabaseAccessor, DocumentDataStorage}
import pl.mpieciukiewicz.codereview.utils.json.JsonUtil
import pl.mpieciukiewicz.codereview.system.{ProjectManager, RepositoryManager, DocumentsCache}
import pl.mpieciukiewicz.codereview.utils.{PasswordUtil, RandomGenerator, Configuration}
import com.typesafe.config.ConfigFactory
import java.io.InputStreamReader
import pl.mpieciukiewicz.codereview.utils.clock.DefaultTimeZoneClock

/**
 *
 */
class Container {

  private val MAIN_CONFIG_FILE = "/application.conf"
  val configuration = Configuration.fromClasspath(MAIN_CONFIG_FILE)

  val passwordUtil = new PasswordUtil(configuration.security.systemSalt)
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

  val documentsCache = new DocumentsCache()


  val repositoryManager = new RepositoryManager(repositoryStorage, commitStorage, randomUtil, configuration, clock)
  val projectManager = new ProjectManager(projectStorage, repositoryStorage)

}

object Container {
  var instance = new Container
}
