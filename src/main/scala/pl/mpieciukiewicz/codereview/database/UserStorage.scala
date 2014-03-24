package pl.mpieciukiewicz.codereview.database

import pl.mpieciukiewicz.codereview.model.User
import pl.mpieciukiewicz.codereview.database.engine.DocumentDataStorage

/**
 *
 */
class UserStorage(val dds: DocumentDataStorage) {

  import dds._

  def add(user: User): User = {
    saveNewEntity(user.copy(id = Some(loadNewId())))
  }

  def loadAll(): List[User] = {
    loadAllEntitiesByType(classOf[User])
  }

  def findByName(name: String): Option[User] = {
    loadAllEntitiesByType(classOf[User]).find(_.name == name)
  }

  def findByEmail(email: String): Option[User] = {
    loadAllEntitiesByType(classOf[User]).find(_.email == email)
  }

}
