package pl.mpieciukiewicz.codereview.system.actor

import pl.mpieciukiewicz.codereview.database.ProjectStorage
import akka.actor.Actor
import pl.mpieciukiewicz.codereview.model.Project
import scala.util.{Failure, Success}
import pl.mpieciukiewicz.codereview.model.client.ProjectWithRepositories
import pl.mpieciukiewicz.codereview.system.ProjectManager

object ProjectManagerActor {

  case class CreateProject(projectName: String)

  case class ProjectCreated(successful: Boolean, projectId: Option[Int] = None)

  case class LoadUserProjects(userId: Int)
  case class UserProjects(projects: List[Project])

  case class LoadProject(projectId: Int)
  case class ProjectResponse(project: Option[Project])

  case class LoadUserProjectsAndRepositories(userId: Int)
  case class UserProjectsAndRepositories(projects: List[ProjectWithRepositories])

}


class ProjectManagerActor(worker: ProjectManager) extends Actor {

  import ProjectManagerActor._

  override def receive: Receive = {
    case msg: CreateProject => worker.createProject(msg.projectName) match {
      case Success(projectId) => sender ! ProjectCreated(true, Some(projectId))
      case Failure(reason) => ProjectCreated(false)
    }
    case msg: LoadUserProjects =>
      val projects = worker.loadUserProjects(msg.userId)
      sender ! UserProjects(projects)
    case msg: LoadProject =>
      val project = worker.loadProject(msg.projectId)
      sender ! ProjectResponse(project)
    case msg: LoadUserProjectsAndRepositories =>
      val projects = worker.loadUserProjectsAndRepositories(msg.userId)
      sender ! UserProjectsAndRepositories(projects)
  }

}
