package pl.mpieciukiewicz.codereview.web

import org.scalatra.{Unauthorized, FutureSupport, AsyncResult, ScalatraServlet}
import pl.mpieciukiewicz.codereview.system.{ProjectManager, UserManager, RepositoryManager, DocumentsCache}
import pl.mpieciukiewicz.codereview.ioc.Container
import akka.actor.{ActorSelection, Actor, ActorSystem}
import akka.pattern.ask
import scala.concurrent.{Await, ExecutionContext, Future}
import akka.util.Timeout
import scala.concurrent.duration._
import pl.mpieciukiewicz.codereview.system.UserManager.SessionInfoResponse

/**
 *
 */
class RestServlet(system: ActorSystem) extends ScalatraServlet with FutureSupport {

  val cache = Container.instance.documentsCache

  val jsonUtil = Container.instance.jsonUtil
  import jsonUtil._

  implicit val defaultTimeout = Timeout(30 seconds)

  before() {
    response.setHeader("Cache-control", "no-cache")
  }

  get("/ping") {
    "pong"
  }

  get("/commits/:repository/:start/:count") {
    async {
      cache.getOrInsert(request.getRequestURI) {
        val actor = system.actorSelection("akka://application/user/repositoryManager")
        val msg = RepositoryManager.LoadCommits(params("repository").toInt, params("start").toInt, params("count").toInt)
        actor.askForJson(msg)
      }
    }
  }

  post("/register-user") {
    async {
      val actor = system.actorSelection("akka://application/user/userManager")
      val msg = UserManager.RegisterUser(params("name"), params("email"), params("password"))
      actor.askForJson(msg)
    }
  }

  post("/authenticate-user") {
    async {
      val actor = system.actorSelection("akka://application/user/userManager")
      val msg = UserManager.AuthenticateUser(params("user"), params("password"), request.getRemoteAddr)
      actor.askForJson(msg)
    }
  }

  get("/logout") {
    async {
      val actor = system.actorSelection("akka://application/user/userManager")
      val msg = UserManager.Logout(cookies.get("sessionId").getOrElse(""))
      actor.askForJson(msg)
    }
  }

  post("/add-repository") {
    async {
      val actor = system.actorSelection("akka://application/user/repositoryManager")
      val msg = RepositoryManager.AddRepository(params("cloneUrl"), params("repoName"), params("projectId").toInt)
      actor.askForJson(msg)
    }
  }

  post("/add-project") {
    async {
      val actor = system.actorSelection("akka://application/user/projectManager")
      val msg =  ProjectManager.CreateProject(params("projectName"))
      actor.askForJson(msg)
    }
  }

  get("/project/:projectId") {
    async {
      val actor = system.actorSelection("akka://application/user/projectManager")
      val msg =  ProjectManager.LoadProject(params("projectId").toInt)
      actor.askForJson(msg)
    }
  }

  get("/project/:projectId/repositories") {
    async {
      val actor = system.actorSelection("akka://application/user/repositoryManager")
      val msg =  RepositoryManager.LoadRepositoriesForProject(params("projectId").toInt)
      actor.askForJson(msg)
    }
  }

  get("/user-projects") {
    async {
      val userManager = system.actorSelection("akka://application/user/userManager")
      val sessionId = cookies.get("sessionId")
      val userInfo = userManager ? UserManager.GetSessionInfo(sessionId.getOrElse(""), request.getRemoteAddr)
      val userInfoResponse = Await.result(userInfo, 10 seconds).asInstanceOf[SessionInfoResponse]

      val future:Future[_] = userInfoResponse.userId match {
        case Some(userId) =>
          val actor = system.actorSelection("akka://application/user/projectManager")
          val msg = ProjectManager.LoadUserProjects(userId)
          actor.askForJson(msg)
        case None => Future.successful(Unauthorized())
      }

      future
    }
  }

  def async(block: => Future[_]):AsyncResult = {
    new AsyncResult {
      val is = block
    }
  }

  implicit class MyActor(actor:ActorSelection) {
    def askForJson(msg: Any):Future[String] = {
      actor.ask(msg).map(toJson)
    }
  }

  override protected implicit def executor: ExecutionContext = system.dispatcher
}
