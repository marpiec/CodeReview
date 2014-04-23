package pl.mpieciukiewicz.codereview.system

import pl.mpieciukiewicz.codereview.model.{UserRole, Project}
import pl.mpieciukiewicz.codereview.database.{UserRoleStorage, RepositoryStorage, ProjectStorage}
import scala.util.{Success, Failure, Try}
import pl.mpieciukiewicz.codereview.model.client.ProjectWithRepositories
import pl.mpieciukiewicz.codereview.model.constant.ProjectRole

class ProjectManager(projectStorage: ProjectStorage, userRoleStorage: UserRoleStorage, repositoryStorage: RepositoryStorage) {

  def createProject(projectName: String, ownerUserId: Int):Try[Int] = {
    projectStorage.findByName(projectName) match {
      case Some(project) => Failure(new AlreadyExistsException)
      case None =>
        val newProject = projectStorage.add(Project(None, projectName))
        userRoleStorage.add(UserRole(0, ownerUserId, newProject.id.get, ProjectRole.Owner))
        Success(newProject.id.get)
    }
  }

  def loadUserProjects(userId: Int):List[Project] = {
    val projectsIds = userRoleStorage.findByUser(userId).map(_.projectId)
    projectStorage.loadByProjectsIds(projectsIds)
  }


  def loadProject(projectId: Int):Option[Project] = {
    projectStorage.findById(projectId)
  }

  def loadUserProjectsAndRepositories(userId: Int):List[ProjectWithRepositories] = {
    val projects = loadUserProjects(userId)
    val projectsWithRepositories = projects.map { project =>
        val repositories = repositoryStorage.findByProject(project.id.get)
        ProjectWithRepositories(project, repositories)
    }
    projectsWithRepositories
  }

}
