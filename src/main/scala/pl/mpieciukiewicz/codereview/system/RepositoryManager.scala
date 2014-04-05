package pl.mpieciukiewicz.codereview.system

import pl.mpieciukiewicz.codereview.database.{CommitStorage, RepositoryStorage}
import pl.mpieciukiewicz.codereview.utils.{Configuration, RandomGenerator}
import pl.mpieciukiewicz.codereview.utils.clock.Clock
import java.io.File
import pl.mpieciukiewicz.codereview.vcs.git.{GitReader, GitLocalRepositoryManager}
import pl.mpieciukiewicz.codereview.model.{CommitWithFiles, Repository}



class RepositoryManager(repositoryStorage: RepositoryStorage, commitStorage: CommitStorage, randomUtil: RandomGenerator, config: Configuration, clock: Clock) {

  def addRepository(cloneUrl: String, repositoryName: String, projectId: Int):Int = {

    var repoDirName:String = null
    do {
      repoDirName = randomUtil.generateRepoDirectoryName
    } while (new File(config.storage.dataDirectory, repoDirName).exists())

    val repoDirPath = config.storage.dataDirectory + repoDirName

    new GitLocalRepositoryManager(repoDirPath).cloneRemoteRepository(cloneUrl) //todo externalize and asynchronize
    val repository = repositoryStorage.add(Repository(None, projectId, repositoryName, cloneUrl, repoDirName, clock.now))

    val allGitCommits = new GitReader(repoDirPath).readAllCommits()

    val allCommits = allGitCommits.map(_.convertToCommit(repository.id.get))

    commitStorage.addAll(allCommits)

    repository.id.get
  }

  def loadCommits(repositoryId: Int, start: Int, count: Int):List[CommitWithFiles] = {
    val repository = repositoryStorage.findById(repositoryId)
    val commits = commitStorage.findByRepositoryId(repositoryId)

    val commitFiles = commits.map { commit =>
      CommitWithFiles(commit, new GitReader(config.storage.dataDirectory + repository.get.localDir).readFilesFromCommit(commit.hash))
    }

    commitFiles
  }


  def loadRepositoriesForProject(projectId: Int):List[Repository] = {
    repositoryStorage.findByProject(projectId)
  }


}
