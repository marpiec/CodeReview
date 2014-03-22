package pl.mpieciukiewicz.codereview.vcs.git

import org.scalatest.{BeforeAndAfter, GivenWhenThen, FeatureSpec}
import org.fest.assertions.api.Assertions._
import collection.JavaConverters._
import java.nio.file.{Path, Files}
import org.apache.commons.io.FileUtils
import java.io.File

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

    scenario("Can clone repository from GitLab") {

      Given("Repository remote url and repository manager initialized with local directory")
      val remoteUrl = "https://github.com/marpiec/AngularJSCalculator.git"
      val repositoryManager = new GitLocalRepositoryManager(tmpDir)

      When("Repository is cloned")
      repositoryManager.cloneRemoteRepository(remoteUrl)

      Then("Local directory contains files from repository")
      tmpDir.list().contains("index.html")
    }

    scenario("Can remove repository") {

      Given("Cloned repository")
      val remoteUrl = "https://github.com/marpiec/AngularJSCalculator.git"
      val repositoryManager = new GitLocalRepositoryManager(tmpDir)
      repositoryManager.cloneRemoteRepository(remoteUrl)

      When("Remove repository")
      repositoryManager.removeRepository()

      Then("Directory that has contained local repository does not exists")
      assertThat(tmpDir).doesNotExist()
    }

  }

}
