package pl.mpieciukiewicz.codereview.system.actor

import akka.actor.Actor
import pl.mpieciukiewicz.codereview.model.{FileContent, CommitWithFiles, Repository}
import pl.mpieciukiewicz.codereview.vcs.VcsFileDiff
import pl.mpieciukiewicz.codereview.system.RepositoryManager
import pl.mpieciukiewicz.codereview.ioc.ActorProvider
import pl.mpieciukiewicz.codereview.utils.{Configuration, RandomGenerator}
import pl.mpieciukiewicz.codereview.web.TaskMonitorWithId
import pl.mpieciukiewicz.codereview.vcs.git.JGitProgressMonitor

object RepositoryManagerActor {

  case class AddRepository(cloneUrl: String, repositoryName: String, projectId: Int, taskMonitor: TaskMonitorWithId)
  case class AddingRepositoryResponse(successful: Boolean, repositoryId: Option[Int], taskMonitorId: String)

  case class LoadCommits(repositoryId: Int, start: Int, count: Int)
  case class LoadCommitsResponse(commits: List[CommitWithFiles])

  case class LoadRepositoriesForProject(projectId: Int)
  case class LoadRepositoriesResponse(repositories: List[Repository])

  case class LoadCommit(repositoryId: Int, commitId: Int)
  case class LoadCommitResponse(commit: Option[CommitWithFiles])

  case class LoadFilesContentFromCommit(repositoryId: Int, commitId: Int)
  case class LoadFilesContentFromCommitResponse(files: List[FileContent])

  case class LoadFilesDiffFromCommit(repositoryId: Int, commitId: Int)
  case class LoadFilesDiffFromCommitResponse(files: List[VcsFileDiff])

  case class GetCloningProgress(repositoryId: Int)
  case class CloningProgressResponse(progress: Option[JGitProgressMonitor])

  case class CloningFinished(repositoryId: Int)

}

class RepositoryManagerActor(worker: RepositoryManager, actorProvider: ActorProvider, randomGenerator: RandomGenerator, config: Configuration) extends Actor {

  import RepositoryManagerActor._



  override def receive = {
    case msg: AddRepository =>
      val repositoryId = worker.addRepository(msg.cloneUrl, msg.repositoryName, msg.projectId, msg.taskMonitor.monitor)
      sender ! AddingRepositoryResponse(true, Some(repositoryId), msg.taskMonitor.monitorId)
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
