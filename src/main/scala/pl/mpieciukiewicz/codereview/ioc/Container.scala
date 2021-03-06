package pl.mpieciukiewicz.codereview.ioc

import pl.mpieciukiewicz.codereview.database._
import pl.mpieciukiewicz.codereview.database.engine.{DatabaseAccessor, DocumentDataStorage}
import pl.mpieciukiewicz.codereview.utils.json.JsonUtil
import pl.mpieciukiewicz.codereview.system._
import pl.mpieciukiewicz.codereview.utils.{PasswordUtil, RandomGenerator, Configuration}
import pl.mpieciukiewicz.codereview.utils.clock.DefaultTimeZoneClock
import akka.actor.ActorSystem
import pl.mpieciukiewicz.codereview.web.ProgressMonitor
import pl.mpieciukiewicz.codereview.utils.email.{SslMailSender, TLSMailSender}
import pl.mpieciukiewicz.codereview.StartupInitializer

/**
 *
 */
class Container {



  private val MAIN_CONFIG_FILE = "/application.conf"
  val configuration = Configuration.fromClasspath(MAIN_CONFIG_FILE)

  //Akka
  val actorSystem = ActorSystem("application")
  val actorProvider = new ActorProvider(actorSystem, this)

  val progressMonitor = new ProgressMonitor

  val passwordUtil = new PasswordUtil(configuration.security.systemSalt)
  val clock = new DefaultTimeZoneClock
  val jsonUtil = new JsonUtil
  val randomUtil = new RandomGenerator

  val mailSender = if(configuration.smtp.ssl) {
    new SslMailSender(configuration.smtp.user, configuration.smtp.password, configuration.smtp.host, configuration.smtp.port, configuration.smtp.mailFrom)
  } else {
    new TLSMailSender(configuration.smtp.host, configuration.smtp.port, configuration.smtp.mailFrom)
  }


  val databaseAccessor = new DatabaseAccessor("jdbc:h2:"+configuration.storage.dataDirectory+"/codeReview", "sa", "sa")
  val documentDataStorage = new DocumentDataStorage(databaseAccessor, jsonUtil)
  documentDataStorage.initDatabaseStructure()

  val sequenceManager = new DatabaseSequenceManager(databaseAccessor)
  val userStorage = new UserStorage(databaseAccessor, sequenceManager)
  val projectStorage = new ProjectStorage(databaseAccessor, sequenceManager)
  val userRoleStorage = new UserRoleStorage(databaseAccessor, sequenceManager)
  val repositoryStorage = new RepositoryStorage(databaseAccessor, sequenceManager)
  val commitStorage = new CommitStorage(databaseAccessor, sequenceManager)
  val fileContentStorage = new FileContentStorage(databaseAccessor, sequenceManager, jsonUtil)

  val documentsCache = new DocumentsCache()


  val repositoryManager = new RepositoryManager(repositoryStorage, commitStorage, fileContentStorage, randomUtil, configuration, clock)
  val projectManager = new ProjectManager(projectStorage, userRoleStorage, repositoryStorage, userStorage)
  val userManager = new UserManager(userStorage, randomUtil, clock, passwordUtil, mailSender, userRoleStorage)


  val startupInitializer = new StartupInitializer(repositoryManager)
}

object Container {
  var instance = new Container
}
