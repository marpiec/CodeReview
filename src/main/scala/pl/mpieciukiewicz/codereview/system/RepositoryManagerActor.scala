package pl.mpieciukiewicz.codereview.system

import akka.actor.Actor
import pl.mpieciukiewicz.codereview.model.{CommitWithFiles, Repository}

object RepositoryManagerActor {

  case class AddRepository(cloneUrl: String, repositoryName: String, projectId: Int)
  case class AddingRepositoryResponse(successful: Boolean, repositoryId: Option[Int])

  case class LoadCommits(repositoryId: Int, start: Int, count: Int)
  case class LoadCommitsResponse(commits: List[CommitWithFiles])

  case class LoadRepositoriesForProject(projectId: Int)
  case class LoadRepositoriesResponse(repositories: List[Repository])
}

class RepositoryManagerActor(worker: RepositoryManager) extends Actor {

  import RepositoryManagerActor._

  override def receive = {
    case msg: AddRepository =>
      val repositoryId = worker.addRepository(msg.cloneUrl, msg.repositoryName, msg.projectId)
      sender ! AddingRepositoryResponse(true, Some(repositoryId))
    case msg: LoadCommits =>
      val commits = worker.loadCommits(msg.repositoryId, msg.start, msg.count)
      sender ! LoadCommitsResponse(commits)
    case msg: LoadRepositoriesForProject =>
      val repositories = worker.loadRepositoriesForProject(msg.projectId)
      sender ! LoadRepositoriesResponse(repositories)
  }

}
