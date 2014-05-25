package pl.mpieciukiewicz.codereview.system

import pl.mpieciukiewicz.codereview.database.{FileContentStorage, CommitStorage, RepositoryStorage}
import pl.mpieciukiewicz.codereview.utils.{Configuration, RandomGenerator}
import pl.mpieciukiewicz.codereview.utils.clock.Clock
import java.io.File
import pl.mpieciukiewicz.codereview.vcs.git.{GitReader, GitLocalRepositoryManager}
import pl.mpieciukiewicz.codereview.model._
import pl.mpieciukiewicz.codereview.model.constant.{LineChangeType, FileChangeType}
import pl.mpieciukiewicz.codereview.vcs.VcsFileContentRename
import pl.mpieciukiewicz.codereview.vcs.VcsFileContentModify
import pl.mpieciukiewicz.codereview.vcs.VcsLineDeleted
import pl.mpieciukiewicz.codereview.vcs.VcsFileContentDelete
import scala.Some
import pl.mpieciukiewicz.codereview.vcs.VcsLineAdded
import pl.mpieciukiewicz.codereview.vcs.VcsFileDiff
import pl.mpieciukiewicz.codereview.vcs.VcsFileContentCopy
import pl.mpieciukiewicz.codereview.vcs.VcsFileContentAdd
import pl.mpieciukiewicz.codereview.web.TaskProgressMonitor
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import pl.mpieciukiewicz.codereview.model.persitent.{Repository, FileContent}
import pl.mpieciukiewicz.codereview.model.client.{LineChange, CommitWithFiles}
import org.slf4j.LoggerFactory


class RepositoryManager(repositoryStorage: RepositoryStorage,
                        commitStorage: CommitStorage,
                        fileContentStorage: FileContentStorage,
                        randomGenerator: RandomGenerator, config: Configuration, clock: Clock) {

  private val log = LoggerFactory.getLogger(classOf[RepositoryManager])

  def addRepository(cloneUrl: String, repositoryName: String, projectId: Int, taskMonitor: TaskProgressMonitor): Int = {

    val repoDirName: String = generateNewUniqueRepoDirName

    val repoDirPath = config.storage.dataDirectory + repoDirName

    Future {
      new GitLocalRepositoryManager(repoDirPath).cloneRemoteRepository(cloneUrl, taskMonitor)
    }


    val repository = repositoryStorage.add(Repository(None, projectId, repositoryName, cloneUrl, repoDirName, clock.now))

    //    val allGitCommits = new GitReader(repoDirPath).readAllCommits()
    //
    //    val allCommits = allGitCommits.map(_.convertToCommit(repository.id.get))
    //
    //    commitStorage.addAll(allCommits)

    repository.id.get
  }

  def generateNewUniqueRepoDirName: String = synchronized {
    var repoDirName: String = null
    do {
      repoDirName = randomGenerator.generateRepoDirectoryName
    } while (!new File(config.storage.dataDirectory, repoDirName).mkdirs())
    repoDirName
  }

  def updateAllRepositories() {
    repositoryStorage.loadAll().foreach {repository =>
      log.info("Updating " + repository.name)
      new GitLocalRepositoryManager(config.storage.dataDirectory + repository.localDir).updateRepository()
    }
  }

  def updateRepository(repositoryId: Int, taskMonitor: TaskProgressMonitor) {
    repositoryStorage.findById(repositoryId) match {
      case Some(repository) => new GitLocalRepositoryManager(config.storage.dataDirectory + repository.localDir).updateRepository()
      case None => throw new IllegalArgumentException("Repository "+repositoryId+" does not exists!")
    }
  }


  def loadCommits(repositoryId: Int, start: Int, count: Int): List[CommitWithFiles] = {
    val repository = repositoryStorage.findById(repositoryId)
    val commitsFromDB = commitStorage.findByRepositoryId(repositoryId, start, count)

    val commits = if(commitsFromDB.isEmpty) {
      val allGitCommits = new GitReader(config.storage.dataDirectory + repository.get.localDir).readCommits(start, count)
      val allCommits = allGitCommits.map(_.convertToCommit(repositoryId))
      commitStorage.addAll(allCommits)
    } else {
      commitsFromDB
    }

    val commitFiles = commits.map {
      commit =>
        CommitWithFiles(commit, new GitReader(config.storage.dataDirectory + repository.get.localDir).readFilesFromCommit(commit.hash))
    }

    commitFiles
  }

  def loadCommit(repositoryId: Int, commitId: Int): Option[CommitWithFiles] = {
    val repository = repositoryStorage.findById(repositoryId)
    val commit = commitStorage.findById(commitId)

    val commitFiles = commit.map {
      commit =>
        CommitWithFiles(commit, new GitReader(config.storage.dataDirectory + repository.get.localDir).readFilesFromCommit(commit.hash))
    }

    commitFiles
  }

  def loadFilesContentFromCommit(repositoryId: Int, commitId: Int): List[FileContent] = {

    val filesContents = fileContentStorage.findByCommit(commitId)

    if (filesContents.isEmpty) {
      val repository = repositoryStorage.findById(repositoryId)
      val commit = commitStorage.findById(commitId)
      val filesContentsFromGit = new GitReader(config.storage.dataDirectory + repository.get.localDir).readFilesContentFromCommit(commit.get.hash)

      val newFilesContents: List[FileContent] = filesContentsFromGit.map {
        case (VcsFileContentAdd(content, path), diff) => FileContent(0, commitId, None, None, Some(content), Some(path), toLineChanges(diff), FileChangeType.Add)
        case (VcsFileContentModify(fromContent, path, toContent), diff) => FileContent(0, commitId, Some(fromContent), Some(path), Some(toContent), None, toLineChanges(diff), FileChangeType.Modify)
        case (VcsFileContentDelete(oldContent, oldPath), diff) => FileContent(0, commitId, Some(oldContent), Some(oldPath), None, None, toLineChanges(diff), FileChangeType.Delete)
        case (VcsFileContentRename(fromContent, fromPath, toContent, toPath), diff) => FileContent(0, commitId, Some(fromContent), Some(fromPath), Some(toContent), Some(toPath), toLineChanges(diff), FileChangeType.Rename)
        case (VcsFileContentCopy(fromContent, fromPath, toContent, toPath), diff) => FileContent(0, commitId, Some(fromContent), Some(fromPath), Some(toContent), Some(toPath), toLineChanges(diff), FileChangeType.Copy)
      }

      fileContentStorage.addAll(newFilesContents)
    } else {
      filesContents
    }

  }

  private def toLineChanges(fileDiff: VcsFileDiff): List[LineChange] = {
    fileDiff.changedLines.map {
      case VcsLineDeleted(number, content) => LineChange(number, content, LineChangeType.Delete)
      case VcsLineAdded(number, content) => LineChange(number, content, LineChangeType.Add)
    }
  }

  def loadFilesDiffFromCommit(repositoryId: Int, commitId: Int): List[VcsFileDiff] = {
    val repository = repositoryStorage.findById(repositoryId)
    val commit = commitStorage.findById(commitId)
    new GitReader(config.storage.dataDirectory + repository.get.localDir).readFilesDiffFromCommit(commit.get.hash)
  }


  def loadRepositoriesForProject(projectId: Int): List[Repository] = {
    repositoryStorage.findByProject(projectId)
  }


}
