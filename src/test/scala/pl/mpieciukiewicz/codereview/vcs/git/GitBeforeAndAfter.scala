package pl.mpieciukiewicz.codereview.vcs.git

import org.scalatest.{Suite, BeforeAndAfter, FeatureSpec, GivenWhenThen}
import java.nio.file.Files
import java.io.File
import org.apache.commons.io.FileUtils

trait GitBeforeAndAfter extends BeforeAndAfter { this: Suite =>

  var repoDirectory:File = _
  
  before {
    val sampleRepositoryBundleUri = "file://"+System.getProperty("user.dir")+"/testdata/AngularJSCalculator.bundle"
    repoDirectory = Files.createTempDirectory("CodeReviewTest").toFile
    new GitLocalRepositoryManager(repoDirectory.getAbsolutePath).cloneRemoteRepository(sampleRepositoryBundleUri)
  }
  
  after {
    FileUtils.deleteQuietly(repoDirectory)
  }

}
