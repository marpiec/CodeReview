package pl.mpieciukiewicz.codereview.system

import akka.actor.Actor
import pl.mpieciukiewicz.codereview.model.{CommitWithFiles, Repository}
import pl.mpieciukiewicz.codereview.vcs.{FileDiff, FileContent}

object RepositoryManagerActor {

  case class AddRepository(cloneUrl: String, repositoryName: String, projectId: Int)
  case class AddingRepositoryResponse(successful: Boolean, repositoryId: Option[Int])

  case class LoadCommits(repositoryId: Int, start: Int, count: Int)
  case class LoadCommitsResponse(commits: List[CommitWithFiles])

  case class LoadRepositoriesForProject(projectId: Int)
  case class LoadRepositoriesResponse(repositories: List[Repository])

  case class LoadCommit(repositoryId: Int, commitId: Int)
  case class LoadCommitResponse(commit: Option[CommitWithFiles])

  case class LoadFilesContentFromCommit(repositoryId: Int, commitId: Int)
  case class LoadFilesContentFromCommitResponse(files: List[FileContent])

  case class LoadFilesDiffFromCommit(repositoryId: Int, commitId: Int)
  case class LoadFilesDiffFromCommitResponse(files: List[FileDiff])

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
    case msg: LoadCommit =>
      val commit = worker.loadCommit(msg.repositoryId, msg.commitId)
      sender ! LoadCommitResponse(commit)
    case msg: LoadRepositoriesForProject =>
      val repositories = worker.loadRepositoriesForProject(msg.projectId)
      sender ! LoadRepositoriesResponse(repositories)
    case msg: LoadFilesContentFromCommit =>
      val files = worker.loadFilesContentFromCommit(msg.repositoryId, msg.commitId)
      sender ! LoadFilesContentFromCommitResponse(files)
    case msg: LoadFilesDiffFromCommit =>
      val files = worker.loadFilesDiffFromCommit(msg.repositoryId, msg.commitId)
      sender ! LoadFilesDiffFromCommitResponse(files)

  }

}
