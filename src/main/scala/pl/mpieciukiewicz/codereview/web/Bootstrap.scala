package pl.mpieciukiewicz.codereview.web

import org.scalatra.LifeCycle
import akka.actor.ActorSystem
import pl.mpieciukiewicz.codereview.system.ActorSystemInitializator
import javax.servlet.ServletContext


/**
 *
 */
class Bootstrap extends LifeCycle {

  val system = ActorSystem("application")
  new ActorSystemInitializator(system).createActors()


  override def init(servletContext: ServletContext) {
    servletContext.mount(new RestServlet(system), "/rest/*")

  }

  override def destroy(context: ServletContext) {
    system.shutdown()
  }
}
