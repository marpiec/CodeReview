package pl.mpieciukiewicz.codereview.system

import akka.actor.{ActorSystem, Props, ActorContext}
import pl.mpieciukiewicz.codereview.database.UserStorage
import pl.mpieciukiewicz.codereview.database.engine.{DatabaseAccessor, DocumentDataStorage}
import pl.mpieciukiewicz.codereview.utils.json.JsonUtil
import pl.mpieciukiewicz.codereview.ioc.Container

/**
 * @author Marcin Pieciukiewicz
 */
class ActorSystemInitializator {

  def createActors(context: ActorSystem) {

    context.actorOf(Props(classOf[UserManager], Container.instance.userStorage), "userManager")
    context.actorOf(Props(classOf[RepositoryManager], Container.instance.repositoryStorage), "repositoryManager")

  }

}
