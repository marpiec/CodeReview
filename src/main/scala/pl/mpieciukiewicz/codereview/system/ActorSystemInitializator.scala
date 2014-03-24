package pl.mpieciukiewicz.codereview.system

import akka.actor.{ActorSystem, Props, ActorContext}

/**
 * @author Marcin Pieciukiewicz
 */
class ActorSystemInitializator {

  def createActors(context: ActorSystem) {

    context.actorOf(Props[UserManager], "userManager")

  }

}
