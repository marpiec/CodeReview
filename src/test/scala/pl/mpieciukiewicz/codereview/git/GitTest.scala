package pl.mpieciukiewicz.codereview.git

import org.scalatest.{GivenWhenThen, FeatureSpec}
import java.io.File
import org.eclipse.jgit.api.Git
import org.apache.commons.io.FileUtils

/**
 * @author Marcin Pieciukiewicz
 */
class GitTest extends FeatureSpec with GivenWhenThen {

  feature("GIT repository cloning") {
    scenario("Clone GitHub repository") {
      Given("Local directory")
      val directory = File.createTempFile("TestGitRepository", "")
      directory.delete()

      When("Repository is cloned")
      Git.cloneRepository().
        setURI("https://github.com/marpiec/AngularJSCalculator.git").
        setDirectory(directory).
        call().
        close()

      Then("Repository contains index.html")
      directory.list().contains("index.html")

      FileUtils.deleteDirectory(directory)


    }
  }

}
