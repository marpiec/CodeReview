package pl.mpieciukiewicz.codereview.web

import akka.actor._
import akka.pattern.ask

import spray.routing._
import pl.mpieciukiewicz.codereview.web.json.JsonDirectives
import pl.mpieciukiewicz.codereview.system.{RepositoryManager, UserManager}


class DefaultRouter extends HttpService with Actor with JsonDirectives {

  // we use the enclosing ActorContext's or ActorSystem's dispatcher for our Futures and Scheduler
  implicit def executionContext = actorRefFactory.dispatcher


  def actorRefFactory = context

  def receive = runRoute(route)

  val route = {
    pathPrefix("rest") {
      get {
        path("") {
          complete("This is index!")
        } ~
        path("ping") {
          complete("pong")
        }
      } ~
      post {
        path("register-user") {
          parameters("name", "email", "password") { (name, email, password) =>
            complete {
              askActor(context.actorSelection("akka://application/user/userManager"), UserManager.RegisterUser(name, email, password))
            }
          }
        } ~
        path("authenticate-user") {
          parameters("user", "password") { (user, password) =>
              complete {
                askActor(context.actorSelection("akka://application/user/userManager"), UserManager.AuthenticateUser(user, password))
            }
          }
        } ~
        path("add-repository") {
          parameters("cloneUrl", "repoName", "projectId".as[Int]) { (cloneUrl, repoName, projectId) =>
            complete {
              askActor(context.actorSelection("akka://application/user/repositoryManager"), RepositoryManager.AddRepository(cloneUrl, repoName, projectId))
            }
          }
        }
      }
    }
  }




}



