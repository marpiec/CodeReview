package pl.mpieciukiewicz.codereview.ioc

import akka.actor.{Props, ActorSystem}
import pl.mpieciukiewicz.codereview.system.actor.{UserManagerActor, RepositoryManagerActor, ProjectManagerActor}
import org.eclipse.jgit.lib.ProgressMonitor
import pl.mpieciukiewicz.codereview.utils.RandomGenerator

class ActorProvider(context: ActorSystem, container: Container) {


  lazy val userManagerActor = context.actorOf(Props(classOf[UserManagerActor], container.userManager), "userManager")

  lazy val repositoryManagerActor = context.actorOf(Props(classOf[RepositoryManagerActor], container.repositoryManager, container.actorProvider, container.randomUtil, container.configuration), "repositoryManager")

  lazy val projectManagerActor = context.actorOf(Props(classOf[ProjectManagerActor], container.projectManager), "projectManager")

}
