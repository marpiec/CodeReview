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

//  val ACCESS_LOGGER_NAME = "accessLogger"
//
//  def startWebServer() {
//    val server = new Server(Configuration.instance.webServer.socket)
//
//    val servletContext: ServletContextHandler = createServletContext(server)
//    val staticFilesContext: ContextHandler = createStaticFilesContext("/*", "/static", "index.html")
//    val requestLogHandler = createRequestLogHandler()
//
//    val handlers = new HandlerList()
//    handlers.setHandlers(Array(staticFilesContext, requestLogHandler, servletContext))
//    server.setHandler(handlers)
//
//    server.start()
//    println("Starting jetty...")
//    server.join()
//  }
//
//
//  private def createServletContext(server: Server): ServletContextHandler = {
//    val servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS)
//
//    servletContext.addServlet(classOf[RestServlet], "/rest/*")
//    addH2ConsoleServlet(servletContext, "/h2console/*")
//
//    servletContext
//  }
//
//  private def addH2ConsoleServlet(servletContext: ServletContextHandler, path: String) {
//    val servlet = servletContext.addServlet(classOf[WebServlet], path)
//    servlet.setInitParameter("webAllowOthers", "")
//    servlet.setInitParameter("trace", "")
//    servlet.setInitParameter("db.url", DataStorage.mainDatabaseUrl)
//    servlet.setInitParameter("db.user", Configuration.instance.database.user)
//    servlet.setInitParameter("db.password", Configuration.instance.database.password)
//  }
//
//
//  private def createStaticFilesContext(path: String, classpathPath: String, welcomeFile: String): ContextHandler = {
//    val resourceHandler = new ResourceHandler()
//
//    if (System.getProperty("developmentMode") == "true") {
//      resourceHandler.setBaseResource(Resource.newResource(new File("src/main/resources/static")))
//    } else {
//      resourceHandler.setBaseResource(Resource.newClassPathResource(classpathPath))
//    }
//
//    resourceHandler.setDirectoriesListed(false)
//    resourceHandler.setWelcomeFiles(Array(welcomeFile))
//    val staticFilesContext = new ContextHandler(path)
//    staticFilesContext.setHandler(resourceHandler)
//    staticFilesContext
//  }
//
//  private def createRequestLogHandler(): RequestLogHandler = {
//    val requestLog = new Slf4jRequestLog()
//
//    requestLog.setLoggerName(ACCESS_LOGGER_NAME)
//    requestLog.setLogDateFormat("yyyy-MM-dd hh:mm:ss:SSS")
//    requestLog.setExtended(false)
//
//    val requestLogHandler = new RequestLogHandler()
//    requestLogHandler.setRequestLog(requestLog)
//    requestLogHandler
//  }

}
