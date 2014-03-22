package pl.mpieciukiewicz.codereview.vcs.git

import java.io.File
import org.eclipse.jgit.api.Git
import org.apache.commons.io.FileUtils

/**
 *
 */
class GitLocalRepositoryManager(localDirectory: File) {

  def cloneRemoteRepository(remoteUrl: String) {
    Git.cloneRepository().
      setURI(remoteUrl).
      setDirectory(localDirectory).
      call().
      close()
  }

  def removeRepository() = {
    FileUtils.deleteDirectory(localDirectory)
  }

}
