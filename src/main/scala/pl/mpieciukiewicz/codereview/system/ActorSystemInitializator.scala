package pl.mpieciukiewicz.codereview.system

import akka.actor.{ActorSystem, Props, ActorContext}
import pl.mpieciukiewicz.codereview.database.UserStorage
import pl.mpieciukiewicz.codereview.database.engine.{DatabaseAccessor, DocumentDataStorage}
import pl.mpieciukiewicz.codereview.utils.json.JsonUtil

/**
 * @author Marcin Pieciukiewicz
 */
class ActorSystemInitializator {

  def createActors(context: ActorSystem) {


    val documentDataStorage = new DocumentDataStorage(new DatabaseAccessor("jdbc:h2:mem:testdb", "sa", "sa"), new JsonUtil)

    val userStorage = new UserStorage(documentDataStorage)

    context.actorOf(Props(classOf[UserManager], userStorage), "userManager")

  }

}
