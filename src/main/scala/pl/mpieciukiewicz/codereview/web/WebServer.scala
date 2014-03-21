package pl.mpieciukiewicz.codereview.web

import java.io.File
import org.eclipse.jetty.server.handler.{ContextHandler, ResourceHandler, HandlerList}
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletHolder, ServletContextHandler}
import org.eclipse.jetty.util.resource.Resource
import spray.servlet.{Initializer, Servlet30ConnectorServlet}


/**
 * @author Marcin Pieciukiewicz
 */
class WebServer {

  def start() {
    val server = new Server(8080)

    val servletContext: ServletContextHandler = createSprayServletContext(server, "/rest/*")
    val staticFilesContext: ContextHandler = createStaticFilesContext("/*", "/static", "index.html")

    val handlers = new HandlerList()
    handlers.setHandlers(Array(servletContext, staticFilesContext))
    server.setHandler(handlers)

    server.start()
    server.join()
  }


  private def createSprayServletContext(server: Server, path: String): ServletContextHandler = {
    val servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS)

    servletContext.addEventListener(createSprayInitializer())
    servletContext.addServlet(createSprayServlet(), path)
    servletContext
  }

  private def createSprayInitializer() = {
    new Initializer()
  }

  private def createSprayServlet(): ServletHolder = {
    val sprayServletHolder = new ServletHolder(new Servlet30ConnectorServlet())
    sprayServletHolder.setAsyncSupported(true)
    sprayServletHolder
  }

  private def createStaticFilesContext(path: String, classpathPath: String, welcomeFile: String): ContextHandler = {
    val resourceHandler = new ResourceHandler()
    resourceHandler.setBaseResource(Resource.newResource(new File("src/main/resources/static")))
    //resourceHandler.setBaseResource(Resource.newClassPathResource(classpathPath))
    resourceHandler.setDirectoriesListed(false)
    resourceHandler.setWelcomeFiles(Array(welcomeFile))
    val staticFilesContext = new ContextHandler(path)
    staticFilesContext.setHandler(resourceHandler)
    staticFilesContext
  }


}
