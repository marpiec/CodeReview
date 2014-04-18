package pl.mpieciukiewicz.codereview.vcs.git

import org.scalatest.{Suite, BeforeAndAfter, FeatureSpec, GivenWhenThen}
import java.nio.file.Files
import java.io.File
import org.apache.commons.io.FileUtils
import org.scalatest.mock.MockitoSugar._
import pl.mpieciukiewicz.codereview.web.TaskProgressMonitor


trait GitBeforeAndAfter extends BeforeAndAfter { this: Suite =>

  var repoDirectory:File = _
  
  before {
    val sampleRepositoryBundleUri = "file://"+System.getProperty("user.dir")+"/testdata/AngularJSCalculator.bundle"
    repoDirectory = Files.createTempDirectory("CodeReviewTest").toFile
    new GitLocalRepositoryManager(repoDirectory.getAbsolutePath).cloneRemoteRepository(sampleRepositoryBundleUri, mock[TaskProgressMonitor])
  }
  
  after {
    FileUtils.deleteQuietly(repoDirectory)
  }

}
