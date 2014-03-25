package pl.mpieciukiewicz.codereview.web

import akka.actor.{Props, ActorSystem}
import pl.mpieciukiewicz.codereview.system.ActorSystemInitializator
import spray.servlet.WebBoot

/**
 *
 */
class Boot extends WebBoot {
  // we need an ActorSystem to host our application in
  val system = ActorSystem("application")

  // the service actor replies to incoming HttpRequests
  val serviceActor = system.actorOf(Props[DefaultRouter], "listener")

  new ActorSystemInitializator().createActors(system)

  system.registerOnTermination {
    // put additional cleanup code here
    system.log.info("Application shut down")
  }
}
