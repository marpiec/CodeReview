package pl.mpieciukiewicz.codereview.web

import akka.actor._
import akka.util.Timeout

import spray.routing._
import pl.mpieciukiewicz.codereview.web.json.JsonDirectives


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
      }
    }
  }




}



