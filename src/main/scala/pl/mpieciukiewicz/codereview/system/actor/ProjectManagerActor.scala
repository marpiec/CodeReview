package pl.mpieciukiewicz.codereview.system.actor

import pl.mpieciukiewicz.codereview.database.ProjectStorage
import akka.actor.Actor
import scala.util.{Failure, Success}
import pl.mpieciukiewicz.codereview.model.client.ProjectWithRepositories
import pl.mpieciukiewicz.codereview.system.ProjectManager
import pl.mpieciukiewicz.codereview.model.constant.ProjectRole
import pl.mpieciukiewicz.codereview.model.persitent.Project

object ProjectManagerActor {



  case class CreateProject(projectName: String, ownerUserId: Int)

  case class ProjectCreated(successful: Boolean, projectId: Option[Int] = None)

  case class LoadUserProjects(userId: Int)
  case class UserProjects(projects: List[Project])

  case class LoadProject(projectId: Int)
  case class ProjectResponse(project: Option[Project])

  case class LoadUserProjectsAndRepositories(userId: Int)
  case class UserProjectsAndRepositories(projects: List[ProjectWithRepositories])

  case class AddUserToProject(projectId: Int, userId: Int, role: ProjectRole)
  case class ChangeUserRole(requestorUserId: Int, projectId: Int, userId: Int, role: ProjectRole)
  case class RemoveUserFromProject(projectId: Int, userId: Int)

  case class FindUserRole(projectId: Int, userId: Int)
  case class FindUserRoleResponse(role: Option[ProjectRole])

}


class ProjectManagerActor(worker: ProjectManager) extends Actor {

  import ProjectManagerActor._

  override def receive: Receive = {
    case msg: CreateProject => worker.createProject(msg.projectName, msg.ownerUserId) match {
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
    case msg: AddUserToProject => worker.addUserToProject(msg.projectId, msg.userId, msg.role)
    case msg: ChangeUserRole => worker.changeUserRole(msg.requestorUserId, msg.projectId, msg.userId, msg.role)
    case msg: RemoveUserFromProject => worker.removeUserFromProject(msg.projectId, msg.userId)
    case msg: FindUserRole =>
      val role = worker.findUserRole(msg.projectId, msg.userId)
      sender ! FindUserRoleResponse(role)
  }

}
