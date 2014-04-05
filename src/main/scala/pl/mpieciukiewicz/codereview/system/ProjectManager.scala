package pl.mpieciukiewicz.codereview.system

import pl.mpieciukiewicz.codereview.model.Project
import pl.mpieciukiewicz.codereview.database.{RepositoryStorage, ProjectStorage}
import scala.util.{Success, Failure, Try}

class ProjectManager(projectStorage: ProjectStorage, repositoryStorage: RepositoryStorage) {

  def createProject(projectName: String):Try[Int] = {
    projectStorage.findByName(projectName) match {
      case Some(project) => Failure(new AlreadyExistsException)
      case None =>
        val newProject = projectStorage.add(Project(None, projectName))
        Success(newProject.id.get)
    }
  }

  def loadUserProjects(userId: Int):List[Project] = {
    projectStorage.loadAll() //TODO find only user projects
  }


  def loadProject(projectId: Int):Option[Project] = {
    projectStorage.findById(projectId)
  }
}
