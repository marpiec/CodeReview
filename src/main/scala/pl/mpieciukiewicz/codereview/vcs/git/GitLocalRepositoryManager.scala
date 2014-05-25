package pl.mpieciukiewicz.codereview.vcs.git

import java.io.File
import org.eclipse.jgit.api.Git
import org.apache.commons.io.FileUtils
import org.eclipse.jgit.lib.{Repository, ProgressMonitor}
import pl.mpieciukiewicz.codereview.web.TaskProgressMonitor
import org.eclipse.jgit.storage.file.FileRepositoryBuilder

/**
 *
 */
class GitLocalRepositoryManager(repoDirPath: String) {

  private val localDirectoryFile = new File(repoDirPath)

  def cloneRemoteRepository(remoteUrl: String, taskMonitor: TaskProgressMonitor) {
    Git.cloneRepository().
      setURI(remoteUrl).
      setDirectory(localDirectoryFile).
      setProgressMonitor(new JGitProgressMonitor(taskMonitor)).
      setNoCheckout(true).
      call().
      close()
  }

  def updateRepository() {
    println(repoDirPath)
    val repository = new FileRepositoryBuilder().findGitDir(localDirectoryFile).build()
    new Git(repository).pull().call()
  }

  def removeRepository() = {
    FileUtils.deleteDirectory(localDirectoryFile)
  }

}
