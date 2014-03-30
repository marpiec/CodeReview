package pl.mpieciukiewicz.codereview.database

import pl.mpieciukiewicz.codereview.model.{Project, Repository}
import pl.mpieciukiewicz.codereview.database.engine.DocumentDataStorage
import pl.mpieciukiewicz.codereview.model.Project

/**
 *
 */
class ProjectStorage(val dds: DocumentDataStorage) {

  import dds._

  def add(project: Project): Project = {
    saveNewEntity(project.copy(id = Some(loadNewId())))
  }

  def loadAll(): List[Project] = {
    loadAllEntitiesByType(classOf[Project])
  }

  def findById(id: Int): Option[Project] = {
    loadAllEntitiesByType(classOf[Project]).find(project => project.id.get == id)
  }

  def findByName(name: String): Option[Project] = {
    loadAllEntitiesByType(classOf[Project]).find(project => project.name == name)
  }
}
