package pl.mpieciukiewicz.codereview.vcs.git

import akka.actor.Actor


object GitLocalRepositoryManagerActor {

  case class CloneRemoteRepository(remoteUrl: String)
  case object RepositoryCloneSucceeded

  case object RemoveRepository
  case object RepositoryDeletionSucceeded
}


class GitLocalRepositoryManagerActor(localDirectory: String) extends Actor {

  import GitLocalRepositoryManagerActor._

  val worker = new GitLocalRepositoryManager(localDirectory)

  override def receive: Receive = {
    case msg: CloneRemoteRepository => worker.cloneRemoteRepository(msg.remoteUrl)
    case RemoveRepository => worker.removeRepository()
  }

}
