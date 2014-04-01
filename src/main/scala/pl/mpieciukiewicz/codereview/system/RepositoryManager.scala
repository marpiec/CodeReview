package pl.mpieciukiewicz.codereview.system

import pl.mpieciukiewicz.codereview.database.{CommitStorage, RepositoryStorage, UserStorage}
import akka.actor.Actor
import akka.actor.Actor.Receive
import pl.mpieciukiewicz.codereview.vcs.git.{GitReader, GitLocalRepositoryManager}
import java.io.File
import pl.mpieciukiewicz.codereview.model.{Commit, Repository}
import org.joda.time.DateTime
import pl.mpieciukiewicz.codereview.vcs.FileChange
import pl.mpieciukiewicz.codereview.utils.{RandomGenerator, Configuration}
import org.apache.commons.lang3.RandomUtils
import pl.mpieciukiewicz.codereview.utils.clock.Clock

object RepositoryManager {
  case class AddRepository(cloneUrl: String, repositoryName: String, projectId: Int)

  case class AddingRepositoryResponse(successful: Boolean, repositoryId: Option[Int])


  case class LoadCommits(repositoryId: Int, start: Int, count: Int)

  case class CommitWithFiles(info: Commit, files: List[FileChange])
  case class LoadCommitsResponse(commits: List[CommitWithFiles])

  case class LoadRepositoriesForProject(projectId: Int)
  case class LoadRepositoriesResponse(repositories: List[Repository])
}

class RepositoryManager(repositoryStorage: RepositoryStorage, commitStorage: CommitStorage, randomUtil: RandomGenerator, config: Configuration, clock: Clock) extends Actor {

  import RepositoryManager._

  override def receive = {
    case msg: AddRepository => handleAddRepository(msg)
    case msg: LoadCommits => handleLoadCommits(msg)
    case msg: LoadRepositoriesForProject => loadRepositoriesForProject(msg)
  }

  private def handleAddRepository(msg: AddRepository) {

    var repoDirName:String = null
    do {
      repoDirName = randomUtil.generateRepoDirectoryName
    } while (new File(config.storage.dataDirectory, repoDirName).exists())
    
    val repoDirPath = config.storage.dataDirectory + repoDirName

    new GitLocalRepositoryManager(repoDirPath).cloneRemoteRepository(msg.cloneUrl)
    val repository = repositoryStorage.add(Repository(None, msg.projectId, msg.repositoryName, msg.cloneUrl, repoDirName, clock.now))

    val allGitCommits = new GitReader(repoDirPath).readAllCommits()

    val allCommits = allGitCommits.map(_.convertToCommit(repository.id.get))

    commitStorage.addAll(allCommits)


    sender ! AddingRepositoryResponse(true, repository.id)
  }

  private def handleLoadCommits(msg: LoadCommits) {         
    val repository = repositoryStorage.findById(msg.repositoryId)
    val commits = commitStorage.findByRepositoryId(msg.repositoryId)

    val commitFiles = commits.map { commit =>
      CommitWithFiles(commit, new GitReader(config.storage.dataDirectory + repository.get.localDir).readFilesFromCommit(commit.hash))
    }

    sender ! LoadCommitsResponse(commitFiles)
  }


  def loadRepositoriesForProject(msg: LoadRepositoriesForProject) {
    sender ! LoadRepositoriesResponse(repositoryStorage.findByProject(msg.projectId))
  }
}
