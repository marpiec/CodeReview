package pl.mpieciukiewicz.codereview.system

import pl.mpieciukiewicz.codereview.database.{RepositoryStorage, ProjectStorage}
import akka.actor.Actor
import pl.mpieciukiewicz.codereview.model.Project
import pl.mpieciukiewicz.codereview.system.RepositoryManagerActor.LoadRepositoriesForProject
import scala.util.{Failure, Success}

object ProjectManagerActor {

  case class CreateProject(projectName: String)

  case class ProjectCreated(successful: Boolean, projectId: Option[Int] = None)

  case class LoadUserProjects(userId: Int)
  case class UserProjects(projects: List[Project])

  case class LoadProject(projectId: Int)
  case class ProjectResponse(project: Option[Project])

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
  }

}
