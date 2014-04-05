package pl.mpieciukiewicz.codereview.vcs.git

import akka.actor.Actor


object GitLocalRepositoryManagerActor {

  case class CloneRemoteRepository(remoteUrl: String)
  case object RepositoryCloneSucceeded

  case object RemoveRepository
  case object RepositoryDeletionSucceeded
}


class GitLocalRepositoryManagerActor(worker: GitLocalRepositoryManager) extends Actor {

  import GitLocalRepositoryManagerActor._

  override def receive: Receive = {
    case msg: CloneRemoteRepository => worker.cloneRemoteRepository(msg.remoteUrl)
    case RemoveRepository => worker.removeRepository()
  }

}
