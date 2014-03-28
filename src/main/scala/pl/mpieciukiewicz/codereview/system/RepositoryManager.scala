package pl.mpieciukiewicz.codereview.system

import pl.mpieciukiewicz.codereview.database.{CommitStorage, RepositoryStorage, UserStorage}
import akka.actor.Actor
import akka.actor.Actor.Receive
import pl.mpieciukiewicz.codereview.vcs.git.{GitReader, GitLocalRepositoryManager}
import java.io.File
import pl.mpieciukiewicz.codereview.model.{Commit, Repository}
import org.joda.time.DateTime
import pl.mpieciukiewicz.codereview.vcs.FileChange

object RepositoryManager {
  case class AddRepository(cloneUrl: String, repositoryName: String, projectId: Int)

  case class AddingRepositoryResponse(successful: Boolean, repositoryId: Option[Int])


  case class LoadCommits(repositoryId: Int, start: Int, count: Int)

  case class CommitWithFiles(info: Commit, files: List[FileChange])
  case class LoadCommitsResponse(commits: List[CommitWithFiles])
}

class RepositoryManager(repositoryStorage: RepositoryStorage, commitRepository: CommitStorage) extends Actor {

  import RepositoryManager._

  override def receive = {
    case msg: AddRepository => handleAddRepository(msg)
    case msg: LoadCommits => handleLoadCommits(msg)
  }

  private def handleAddRepository(msg: AddRepository) {
    new GitLocalRepositoryManager("c:/codeReview/first").cloneRemoteRepository(msg.cloneUrl)
    val repository = repositoryStorage.add(Repository(None, msg.projectId, msg.repositoryName, msg.cloneUrl, "c:/codeReview/first", DateTime.now))


    val allGitCommits = new GitReader("c:/codeReview/first").readAllCommits()

    val allCommits = allGitCommits.map(_.convertToCommit(repository.id.get))

    commitRepository.addAll(allCommits)


    sender ! AddingRepositoryResponse(true, repository.id)
  }

  private def handleLoadCommits(msg: LoadCommits) {
    val commits = commitRepository.findByRepositoryId(msg.repositoryId)

    val commitFiles = commits.map { commit =>
      CommitWithFiles(commit, new GitReader("c:/codeReview/first").readFilesFromCommit(commit.hash))
    }

    sender ! LoadCommitsResponse(commitFiles)
  }
}
