package pl.mpieciukiewicz.codereview.system

import akka.actor.{ActorSystem, Props}
import pl.mpieciukiewicz.codereview.ioc.Container

/**
 * @author Marcin Pieciukiewicz
 */
class ActorSystemInitializator(context: ActorSystem) {

  def createActors() {
    val ioc = Container.instance
    context.actorOf(Props(classOf[UserManagerActor], new UserManager(ioc.userStorage, ioc.randomUtil, ioc.clock, ioc.passwordUtil)), "userManager")

    context.actorOf(Props(classOf[RepositoryManagerActor], ioc.repositoryManager), "repositoryManager")

    context.actorOf(Props(classOf[ProjectManagerActor], ioc.projectManager), "projectManager")
  }

}
