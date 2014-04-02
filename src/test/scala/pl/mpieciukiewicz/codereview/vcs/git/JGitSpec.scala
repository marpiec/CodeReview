package pl.mpieciukiewicz.codereview.vcs.git

import org.scalatest.{GivenWhenThen, FeatureSpec}
import java.io.File
import org.eclipse.jgit.api.Git
import org.apache.commons.io.FileUtils
import org.apache.commons.compress.archivers.zip.ZipUtil


/**
 * @author Marcin Pieciukiewicz
 */
class JGitSpec extends FeatureSpec with GivenWhenThen {

  feature("GIT repository support") {
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
