package pl.mpieciukiewicz.codereview.database

import pl.mpieciukiewicz.codereview.model.UserRole
import pl.mpieciukiewicz.codereview.database.engine.DocumentDataStorage

/**
 *
 */
class UserRoleStorage(val dds: DocumentDataStorage) {

  import dds._

  def add(role: UserRole): UserRole = {
    saveNewEntity(role.copy(id = Some(loadNewId())))
  }

  def loadAll(): List[UserRole] = {
    loadAllEntitiesByType(classOf[UserRole])
  }

  def findByUserAndProject(userId: Int, projectId: Int): Option[UserRole] = {
    loadAllEntitiesByType(classOf[UserRole]).find(role => role.userId == userId && role.projectId == projectId)
  }

  def findByUser(userId: Int): List[UserRole] = {
    loadAllEntitiesByType(classOf[UserRole]).filter(role => role.userId == userId)
  }

  def findByProject(projectId: Int): List[UserRole] = {
    loadAllEntitiesByType(classOf[UserRole]).filter(role => role.projectId == projectId)
  }

}
