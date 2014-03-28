package pl.mpieciukiewicz.codereview.vcs.git

import java.io.File
import org.eclipse.jgit.api.Git
import org.apache.commons.io.FileUtils

/**
 *
 */
class GitLocalRepositoryManager(localDirectory: String) {

  private val localDirectoryFile = new File(localDirectory)

  def cloneRemoteRepository(remoteUrl: String) {
    Git.cloneRepository().
      setURI(remoteUrl).
      setDirectory(localDirectoryFile).
      call().
      close()
  }

  def removeRepository() = {
    FileUtils.deleteDirectory(localDirectoryFile)
  }

}
