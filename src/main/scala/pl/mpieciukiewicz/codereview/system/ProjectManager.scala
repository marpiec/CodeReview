package pl.mpieciukiewicz.codereview.system

import pl.mpieciukiewicz.codereview.model.{UserRole, Project}
import pl.mpieciukiewicz.codereview.database.{UserStorage, UserRoleStorage, RepositoryStorage, ProjectStorage}
import scala.util.{Success, Failure, Try}
import pl.mpieciukiewicz.codereview.model.client.ProjectWithRepositories
import pl.mpieciukiewicz.codereview.model.constant.ProjectRole
import org.slf4j.LoggerFactory

class ProjectManager(projectStorage: ProjectStorage,
                     userRoleStorage: UserRoleStorage,
                     repositoryStorage: RepositoryStorage,
                     userStorage: UserStorage) extends Authorizator {

  private final val log = LoggerFactory.getLogger(classOf[ProjectManager])

  implicit val implicitUserRoleStorage = userRoleStorage

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

  def removeUserFromProject(projectId: Int, userId: Int) {
    userRoleStorage.removeByProjectAndUser(userId, projectId)
  }

  def changeUserRole(requestorUserId: Int, projectId: Int, userId: Int, role: ProjectRole) {
    requireAdmin(projectId, requestorUserId, authorized = {
      userRoleStorage.findByUserAndProject(userId, projectId) match {
        case Some(userRole) => userRoleStorage.update(userRole.copy(role = role))
        case None => ()
      }
    }, unauthorized = {
      log.warn(s"Unauthorized try to changeUserRole requestor=$requestorUserId, projectId=$projectId, userId=$userId, role=$role")
    })
  }

  def addUserToProject(projectId: Int, userId: Int, role: ProjectRole) {
    userStorage.findById(userId) match {
      case Some(userRole) => userRoleStorage.add(UserRole(0, userId, projectId, role))
      case None => ()
    }
  }

  def findUserRole(projectId: Int, userId: Int): Option[ProjectRole] = {
    userRoleStorage.findByUserAndProject(userId, projectId).map(_.role)
  }

}
