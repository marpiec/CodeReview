package pl.mpieciukiewicz.codereview.system

import pl.mpieciukiewicz.codereview.database.{RepositoryStorage, ProjectStorage}
import akka.actor.Actor
import pl.mpieciukiewicz.codereview.model.Project
import pl.mpieciukiewicz.codereview.system.RepositoryManager.LoadRepositoriesForProject

object ProjectManager {

  case class CreateProject(projectName: String)

  case class ProjectCreated(successful: Boolean, projectId: Option[Int] = None)

  case class LoadUserProjects(userId: Int)
  case class UserProjects(projects: List[Project])

  case class LoadProject(projectId: Int)
  case class ProjectResponse(project: Option[Project])

}


class ProjectManager(projectStorage: ProjectStorage, repositoryStorage: RepositoryStorage) extends Actor {

  import ProjectManager._


  override def receive: Receive = {
    case msg: CreateProject => createProject(msg)
    case msg: LoadUserProjects => loadUserProjects(msg)
    case msg: LoadProject => loadProject(msg)
  }


  def createProject(msg: CreateProject) {
    projectStorage.findByName(msg.projectName) match {
      case Some(project) => sender ! ProjectCreated(false)
      case None =>
        val newProject = projectStorage.add(Project(None, msg.projectName))
        sender ! ProjectCreated(true, newProject.id)
    }


  }

  def loadUserProjects(msg: LoadUserProjects) {
    val projects = projectStorage.loadAll() //TODO find only user projects
    sender ! UserProjects(projects)
  }


  def loadProject(msg: LoadProject) {
    val project = projectStorage.findById(msg.projectId)
    sender ! ProjectResponse(project)
  }


}
