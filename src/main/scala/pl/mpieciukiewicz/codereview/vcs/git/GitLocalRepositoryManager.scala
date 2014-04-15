package pl.mpieciukiewicz.codereview.vcs.git

import java.io.File
import org.eclipse.jgit.api.Git
import org.apache.commons.io.FileUtils
import org.eclipse.jgit.lib.ProgressMonitor
import pl.mpieciukiewicz.codereview.web.TaskProgressMonitor

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
      call().
      close()
  }

  def removeRepository() = {
    FileUtils.deleteDirectory(localDirectoryFile)
  }

}
