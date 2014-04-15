package pl.mpieciukiewicz.codereview.web

import org.scalatra.LifeCycle
import javax.servlet.ServletContext
import pl.mpieciukiewicz.codereview.ioc.Container


/**
 *
 */
class Bootstrap extends LifeCycle {

  val container = Container.instance

  override def init(servletContext: ServletContext) {
    servletContext.mount(new RestServlet(container.actorSystem, container.actorProvider, container.progressMonitor), "/rest/*")
  }

  override def destroy(context: ServletContext) {
    container.actorSystem.shutdown()
  }
}
