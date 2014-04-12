package pl.mpieciukiewicz.codereview.system

import pl.mpieciukiewicz.codereview.model.Project
import pl.mpieciukiewicz.codereview.database.{RepositoryStorage, ProjectStorage}
import scala.util.{Success, Failure, Try}
import pl.mpieciukiewicz.codereview.model.client.ProjectWithRepositories

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

  def loadUserProjectsAndRepositories(userId: Int):List[ProjectWithRepositories] = {
    val projects = projectStorage.loadAll() //TODO find only user projects
    val projectsWithRepositories = projects.map { project =>
        val repositories = repositoryStorage.findByProject(project.id.get)
        ProjectWithRepositories(project, repositories)
    }
    projectsWithRepositories
  }

}
