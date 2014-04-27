package pl.mpieciukiewicz.codereview.system

import pl.mpieciukiewicz.codereview.database.UserRoleStorage
import pl.mpieciukiewicz.codereview.model.constant.ProjectRole

trait Authorizator {

  def requireOwner[T](projectId:Int, userId: Int, authorized: => T, unauthorized: => T)(implicit userRoleStorage: UserRoleStorage) {
    userRoleStorage.findByUserAndProject(userId, projectId).map(_.role) match {
      case Some(ProjectRole.Owner) => authorized
      case _ => unauthorized
    }
  }

  def requireAdmin[T](projectId:Int, userId: Int, authorized: => T, unauthorized: => T)(implicit userRoleStorage: UserRoleStorage) {
    userRoleStorage.findByUserAndProject(userId, projectId).map(_.role) match {
      case Some(ProjectRole.Owner) => authorized
      case Some(ProjectRole.Admin) => authorized
      case _ => unauthorized
    }
  }

  def requireDeveloper[T](projectId:Int, userId: Int, authorized: => T, unauthorized: => T)(implicit userRoleStorage: UserRoleStorage) {
    userRoleStorage.findByUserAndProject(userId, projectId).map(_.role) match {
      case Some(ProjectRole.Owner) => authorized
      case Some(ProjectRole.Admin) => authorized
      case Some(ProjectRole.Developer) => authorized
      case _ => unauthorized
    }
  }

}
