package pl.mpieciukiewicz.codereview.web

import org.scalatra.{FutureSupport, AsyncResult, ScalatraServlet}
import pl.mpieciukiewicz.codereview.system.{UserManager, RepositoryManager, DocumentsCache}
import pl.mpieciukiewicz.codereview.ioc.Container
import akka.actor.{ActorSelection, Actor, ActorSystem}
import akka.pattern.ask
import scala.concurrent.{ExecutionContext, Future}
import pl.mpieciukiewicz.codereview.system.RepositoryManager.LoadCommits
import akka.util.Timeout
import scala.concurrent.duration._

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

  get("/load-commits/:repository/:start/:count") {
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
      val msg = UserManager.AuthenticateUser(params("user"), params("password"))
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