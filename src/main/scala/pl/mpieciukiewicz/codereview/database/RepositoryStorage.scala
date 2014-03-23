package pl.mpieciukiewicz.codereview.database

import pl.mpieciukiewicz.codereview.model.Project
import pl.mpieciukiewicz.codereview.database.engine.DocumentDataStorage
import pl.mpieciukiewicz.codereview.model.Repository

/**
 *
 */
class RepositoryStorage(val dds: DocumentDataStorage) {

  import dds._

  def add(repository: Repository): Repository = {
    saveNewEntity(repository.copy(id = Some(loadNewId())))
  }

  def loadAll(): List[Repository] = {
    loadAllEntitiesByType(classOf[Repository])
  }

  def findById(id: Int): Option[Repository] = {
    loadAllEntitiesByType(classOf[Repository]).find(repository => repository.id.get == id)
  }

  def findByProject(projectId: Int): List[Repository] = {
    loadAllEntitiesByType(classOf[Repository]).filter(repository => repository.projectId == projectId)
  }

}
