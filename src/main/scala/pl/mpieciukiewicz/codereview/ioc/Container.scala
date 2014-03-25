package pl.mpieciukiewicz.codereview.ioc

import pl.mpieciukiewicz.codereview.database.{RepositoryStorage, UserRoleStorage, ProjectStorage, UserStorage}
import pl.mpieciukiewicz.codereview.database.engine.{DatabaseAccessor, DocumentDataStorage}
import pl.mpieciukiewicz.codereview.utils.json.JsonUtil

/**
 *
 */
class Container {

  val jsonUtil = new JsonUtil
  val databaseAccessor = new DatabaseAccessor("jdbc:h2:data/codeReview", "sa", "sa")
  val documentDataStorage = new DocumentDataStorage(databaseAccessor, jsonUtil)
  documentDataStorage.initDatabaseStructure()

  val userStorage = new UserStorage(documentDataStorage)
  val projectStorage = new ProjectStorage(documentDataStorage)
  val userRoleStorage = new UserRoleStorage(documentDataStorage)
  val repositoryStorage = new RepositoryStorage(documentDataStorage)

}

object Container {
  var instance = new Container
}
