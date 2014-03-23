package pl.mpieciukiewicz.codereview.database

import pl.mpieciukiewicz.codereview.utils.JsonUtil
import pl.mpieciukiewicz.codereview.model.{Project, User}
import pl.mpieciukiewicz.codereview.database.engine.DocumentDataStorage

/**
 *
 */
class UserStorage(val dds: DocumentDataStorage) {

  import dds._

  def addUser(user: User): User = {
    saveNewEntity(user.copy(id = loadNewId()))
  }

  def loadAllUsers():List[User] = {
    loadAllEntitiesByType(classOf[User])
  }

  def findUserByName(userName: String):Option[User] = {
    loadAllEntitiesByType(classOf[User]).find(_.name == userName)
  }
  
}
