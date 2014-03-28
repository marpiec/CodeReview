package pl.mpieciukiewicz.codereview.web

import java.io.File
import org.eclipse.jetty.server.handler.{RequestLogHandler, ContextHandler, ResourceHandler, HandlerList}
import org.eclipse.jetty.server.{Slf4jRequestLog, Server}
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.util.resource.Resource
import org.h2.server.web.WebServlet
import org.scalatra.servlet.ScalatraListener


/**
 * @author Marcin Pieciukiewicz
 */
class WebServer {

  def start() {
    val server = new Server(8080)

    val servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS)
    servletContext.setInitParameter(ScalatraListener.LifeCycleKey, "pl.mpieciukiewicz.codereview.web.Bootstrap")
    servletContext.addEventListener(new ScalatraListener)

    addH2ConsoleServlet(servletContext, "/h2/*")

    val staticFilesContext: ContextHandler = createStaticFilesContext("/*", "/static", "index.html")

    val handlers = new HandlerList()
    handlers.setHandlers(Array(createRequestLogHandler(), servletContext, staticFilesContext))
    server.setHandler(handlers)

    server.start()
    server.join()
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
    if (System.getProperty("developmentMode") == "true") {
      resourceHandler.setBaseResource(Resource.newResource(new File("src/main/resources/static")))
    } else {
      resourceHandler.setBaseResource(Resource.newClassPathResource(classpathPath))
    }

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
    requestLog.setIgnorePaths(Array("/lib/*", "/app/*"))

    val requestLogHandler = new RequestLogHandler()
    requestLogHandler.setRequestLog(requestLog)
    requestLogHandler
  }


}
