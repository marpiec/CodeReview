package pl.mpieciukiewicz.codereview.web

import org.scalatra.{Unauthorized, FutureSupport, AsyncResult, ScalatraServlet}
import pl.mpieciukiewicz.codereview.system._
import pl.mpieciukiewicz.codereview.ioc.{ActorProvider, Container}
import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import scala.concurrent.{Await, ExecutionContext, Future}
import akka.util.Timeout
import scala.concurrent.duration._
import pl.mpieciukiewicz.codereview.system.actor.{ProjectManagerActor, UserManagerActor, RepositoryManagerActor}
import pl.mpieciukiewicz.codereview.system.actor.UserManagerActor.CheckSessionResponse
import pl.mpieciukiewicz.codereview.model.UserRole
import pl.mpieciukiewicz.codereview.model.constant.ProjectRole
import pl.mpieciukiewicz.codereview.utils.protectedid.IdProtectionUtil

/**
 *
 */
class RestServlet(actorSystem: ActorSystem, actorProvider: ActorProvider, progressMonitor: ProgressMonitor) extends ScalatraServlet with FutureSupport {

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
      authenticated {
        userId =>
          cache.getOrInsert(request.getRequestURI) {
            val actor = actorProvider.repositoryManagerActor
            val msg = RepositoryManagerActor.LoadCommits(params("repository").toInt, params("start").toInt, params("count").toInt)
            actor.askForJson(msg)
          }
      }
    }
  }

  post("/register-user") {
    async {
      val actor = actorProvider.userManagerActor
      val msg = UserManagerActor.RegisterUser(params("name"), params("email"))
      actor.askForJson(msg)
    }
  }

  post("/change-user-password") {
    async {
      authenticated {
        userId =>
          val actor = actorProvider.userManagerActor
          val msg = UserManagerActor.ChangeUserPassword(userId, params("oldPassword"), params("newPassword"))
          actor.askForJson(msg)
      }
    }
  }

  post("/forgot-password") {
    async {
        val actor = actorProvider.userManagerActor
        val msg = UserManagerActor.ForgotPassword(params("user"))
        actor ! msg
        Future.successful("ok")
    }
  }

  post("/authenticate-user") {
    async {
      val actor = actorProvider.userManagerActor
      val msg = UserManagerActor.AuthenticateUser(params("user"), params("password"), request.getRemoteAddr)
      actor.askForJson(msg)
    }
  }

  get("/logout") {
    async {
      val actor = actorProvider.userManagerActor
      val msg = UserManagerActor.Logout(cookies.get("sessionId").getOrElse(""))
      actor.askForJson(msg)
    }
  }

  post("/add-repository") {
    async {
      authenticated {
        userId =>
          val taskMonitorWithId = progressMonitor.createTaskProgressMonitor()

          val actor = actorProvider.repositoryManagerActor
          val msg = RepositoryManagerActor.AddRepository(params("cloneUrl"), params("repoName"), params("projectId").toInt, taskMonitorWithId)
          actor.askForJson(msg)
      }
    }
  }

  get("/task-progress/:taskId") {
    progressMonitor.getTaskMonitor(params("taskId")) match {
      case Some(monitor) => toJson(monitor)
      case None => halt(404)
    }
  }

  post("/add-project") {
    async {
      authenticated {
        userId =>
          val actor = actorProvider.projectManagerActor
          val msg = ProjectManagerActor.CreateProject(params("projectName"), userId)
          actor.askForJson(msg)
      }
    }
  }

  get("/current-user-project-role/:projectId") {
    async {
      authenticated {
        userId =>
          val actor = actorProvider.projectManagerActor
          val msg = ProjectManagerActor.FindUserRole(params("projectId").toInt, userId)
          actor.askForJson(msg)
      }
    }
  }

  get("/project/:projectId") {
    async {
      authenticated {
        userId =>
          val actor = actorProvider.projectManagerActor
          val msg = ProjectManagerActor.LoadProject(params("projectId").toInt)
          actor.askForJson(msg)
      }
    }
  }

  get("/project/:projectId/repositories") {
    async {
      authenticated {
        userId =>
          val actor = actorProvider.repositoryManagerActor
          val msg = RepositoryManagerActor.LoadRepositoriesForProject(params("projectId").toInt)
          actor.askForJson(msg)
      }
    }
  }

  get("/project/:projectId/users") {
    async {
      authenticated {
        userId =>
          val actor = actorProvider.userManagerActor
          val msg = UserManagerActor.LoadUsersForProject(params("projectId").toInt)
          actor.askForJson(msg)
      }
    }
  }

  get("/commit/:repositoryId/:commitId") {
    async {
      authenticated {
        userId =>
          val actor = actorProvider.repositoryManagerActor
          val msg = RepositoryManagerActor.LoadCommit(params("repositoryId").toInt, params("commitId").toInt)
          actor.askForJson(msg)
      }
    }
  }

  post("/change-user-role") {
    async {
      authenticated {
        userId =>
          val actor = actorProvider.projectManagerActor
          val msg = ProjectManagerActor.ChangeUserRole(userId, params("projectId").toInt, decryptId(params("userId")), ProjectRole.getByName(params("newRole")))
          actor ! msg
          Future.successful("ok")
      }
    }
  }

  post("/add-user-to-project") {
    async {
      authenticated {
        userId =>
          val actor = actorProvider.projectManagerActor
          val msg = ProjectManagerActor.AddUserToProject(params("projectId").toInt, decryptId(params("userId")), ProjectRole.getByName(params("role")))
          actor ! msg
          Future.successful("ok")
      }
    }
  }

  post("/remove-user-from-project") {
    async {
      authenticated {
        userId =>
          val actor = actorProvider.projectManagerActor
          val msg = ProjectManagerActor.RemoveUserFromProject(params("projectId").toInt, decryptId(params("userId")))
          actor ! msg
          Future.successful("ok")
      }
    }
  }

  get("/find-users/:from/:count/") {
    handleFindUsers(params("from").toInt, params("count").toInt, "")
  }

  get("/find-users/:from/:count/:query") {
    handleFindUsers(params("from").toInt, params("count").toInt, params("query"))
  }

  def handleFindUsers(from: Int, count: Int, query: String) = {
    async {
      authenticated {
        userId =>
          val actor = actorProvider.userManagerActor
          val msg = UserManagerActor.FindUsers(from, count, query)
          actor.askForJson(msg)
      }
    }
  }

  get("/commit-files/:repositoryId/:commitId") {
    async {
      authenticated {
        userId =>
          val actor = actorProvider.repositoryManagerActor
          val msg = RepositoryManagerActor.LoadFilesContentFromCommit(params("repositoryId").toInt, params("commitId").toInt)
          actor.askForJson(msg)
      }
    }
  }

  get("/commit-files-diffs/:repositoryId/:commitId") {
    async {
      authenticated {
        userId =>
          val actor = actorProvider.repositoryManagerActor
          val msg = RepositoryManagerActor.LoadFilesDiffFromCommit(params("repositoryId").toInt, params("commitId").toInt)
          actor.askForJson(msg)
      }
    }
  }

  get("/user-projects") {
    async {
      authenticated { userId =>
        val actor = actorProvider.projectManagerActor
        val msg = ProjectManagerActor.LoadUserProjects(userId)
        actor.askForJson(msg)
      }
    }
  }

  get("/user-projects-and-repositories") {
    async {
      authenticated { userId =>
        val actor = actorProvider.projectManagerActor
        val msg = ProjectManagerActor.LoadUserProjectsAndRepositories(userId)
        actor.askForJson(msg)
      }
    }
  }

  def decryptId(protectedId: String):Int = {
    IdProtectionUtil.decrypt(protectedId).toInt
  }

  def async(block: => Future[_]):AsyncResult = {
    new AsyncResult {
      val is = block
    }
  }

  implicit class MyActor(actor:ActorRef) {
    def askForJson(msg: Any):Future[String] = {
      actor.ask(msg).map(toJson)
    }
  }

  def authenticated(block: Int => Future[_]) = {
    val userManager = actorProvider.userManagerActor
    val sessionId = cookies.get("sessionId")
    val userInfo = userManager ? UserManagerActor.CheckSession(sessionId.getOrElse(""), request.getRemoteAddr)
    val userInfoResponse = Await.result(userInfo, 10 seconds).asInstanceOf[CheckSessionResponse]
    userInfoResponse.userId match {
      case Some(userId) => block(userId)
      case None => Future.successful(Unauthorized())
    }
  }

  override protected implicit def executor: ExecutionContext = actorSystem.dispatcher
}
