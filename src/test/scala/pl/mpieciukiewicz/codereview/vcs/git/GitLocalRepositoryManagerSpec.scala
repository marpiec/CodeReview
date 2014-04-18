package pl.mpieciukiewicz.codereview.vcs.git

import org.scalatest.{BeforeAndAfter, GivenWhenThen, FeatureSpec}
import org.fest.assertions.api.Assertions._
import collection.JavaConverters._
import java.nio.file.{Path, Files}
import org.apache.commons.io.FileUtils
import java.io.File
import org.scalatest.mock.MockitoSugar._
import pl.mpieciukiewicz.codereview.web.TaskProgressMonitor

/**
 *
 */
class GitLocalRepositoryManagerSpec extends FeatureSpec with GivenWhenThen with BeforeAndAfter {

  var tmpDir:File = _


  before {
    tmpDir = Files.createTempDirectory("CodeReviewTest").toFile
  }

  after {
    FileUtils.deleteQuietly(tmpDir)
  }

  feature("Can clone, pull and remove GIT repository") {

    scenario("Can clone repository") {

      Given("Repository remote url and repository manager initialized with local directory")
      //val remoteUrl = "https://github.com/marpiec/AngularJSCalculator.git"

      val remoteUrl = "file://"+System.getProperty("user.dir")+"/testdata/AngularJSCalculator.bundle"
      val repositoryManager = new GitLocalRepositoryManager(tmpDir.getAbsolutePath)

      When("Repository is cloned")
      repositoryManager.cloneRemoteRepository(remoteUrl, mock[TaskProgressMonitor])

      Then("Local directory contains files from repository")
      tmpDir.list().contains("index.html")
    }

    scenario("Can remove repository") {

      Given("Cloned repository")
      val remoteUrl = "file://"+System.getProperty("user.dir")+"/testdata/AngularJSCalculator.bundle"
      val repositoryManager = new GitLocalRepositoryManager(tmpDir.getAbsolutePath)
      repositoryManager.cloneRemoteRepository(remoteUrl, mock[TaskProgressMonitor])

      When("Remove repository")
      repositoryManager.removeRepository()

      Then("Directory that has contained local repository does not exists")
      assertThat(tmpDir).doesNotExist()
    }

  }

}
