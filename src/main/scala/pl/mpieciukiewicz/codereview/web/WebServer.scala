package pl.mpieciukiewicz.codereview.web

import java.io.File
import org.eclipse.jetty.server.handler.{RequestLogHandler, ContextHandler, ResourceHandler, HandlerList}
import org.eclipse.jetty.server.{Slf4jRequestLog, Server}
import org.eclipse.jetty.servlet.{ServletHolder, ServletContextHandler}
import org.eclipse.jetty.util.resource.Resource
import spray.servlet.{Initializer, Servlet30ConnectorServlet}
import org.h2.server.web.WebServlet


/**
 * @author Marcin Pieciukiewicz
 */
class WebServer {

  def start() {
    val server = new Server(8080)

    val servletContext: ServletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS)

    servletContext.addEventListener(createSprayInitializer())
    servletContext.addServlet(createSprayServlet(), "/rest/*")

    addH2ConsoleServlet(servletContext, "/h2console/*")
    
    val staticFilesContext: ContextHandler = createStaticFilesContext("/*", "/static", "index.html")

    val handlers = new HandlerList()
    handlers.setHandlers(Array(servletContext, createRequestLogHandler(), staticFilesContext))
    server.setHandler(handlers)

    server.start()
    server.join()
  }


  private def createSprayInitializer() = {
    new Initializer()
  }

  private def createSprayServlet(): ServletHolder = {
    val sprayServletHolder = new ServletHolder(new Servlet30ConnectorServlet())
    sprayServletHolder.setAsyncSupported(true)
    sprayServletHolder
  }

    private def addH2ConsoleServlet(servletContext: ServletContextHandler, path: String) {
      val servlet = servletContext.addServlet(classOf[WebServlet], path)
      servlet.setInitParameter("webAllowOthers", "")
      servlet.setInitParameter("trace", "")
      servlet.setInitParameter("db.url", "jdbc:h2:data/database")
      servlet.setInitParameter("db.user", "sa")
      servlet.setInitParameter("db.password", "sa")
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

    private def createRequestLogHandler(): RequestLogHandler = {
      val requestLog = new Slf4jRequestLog()

      requestLog.setLoggerName("accessLogger")
      requestLog.setLogDateFormat("yyyy-MM-dd hh:mm:ss:SSS")
      requestLog.setExtended(false)

      val requestLogHandler = new RequestLogHandler()
      requestLogHandler.setRequestLog(requestLog)
      requestLogHandler
    }



}
