package pl.mpieciukiewicz.codereview.git

import org.scalatest.{GivenWhenThen, FeatureSpec}
import pl.mpieciukiewicz.codereview.vcs._
import org.fest.assertions.api.Assertions._
import collection.JavaConverters._
import pl.mpieciukiewicz.codereview.vcs.Commit
import pl.mpieciukiewicz.codereview.vcs.FileModify

/**
 *
 */
class GitReaderSpec extends FeatureSpec with GivenWhenThen {

  feature("GIT repository analysis support") {

    scenario("Get recent commits") {
      Given("Repository and GitReader instance")

      val gitReader = new GitReader("c:/TmpRepo/")

      When("Getting Commits")

      val commits: List[Commit] = gitReader.readCommits(4)


      Then("Have correct commits")

      assertThat(commits.asJava).containsExactly(
        Commit("a6d5e7f8e2e3e9162563bb215bb04bf3a629424a", "Marcin Pieciukiewicz", "Marcin Pieciukiewicz", "Some clean up", 1376576166),
        Commit("e20b7e4df6d4b7df4816a75981331201317e90e8", "Marcin Pieciukiewicz", "Marcin Pieciukiewicz", "Some clean up", 1376554727),
        Commit("98fc0f9e817708f0a4f38f630b134251cf076afc", "Marcin Pieciukiewicz", "Marcin Pieciukiewicz", "Sources", 1376478826),
        Commit("9333a8309c0ec2f6c35d5746861b2b53f8c04955", "Marcin Pieciukiewicz", "Marcin Pieciukiewicz", "Sources", 1376478781))
    }


    scenario("Get files names from commit") {
      Given("Repository and GitReader instance, and commit id")

      val gitReader = new GitReader("c:/TmpRepo/")
      val commitId = "e20b7e4df6d4b7df4816a75981331201317e90e8"

      When("Getting committed files")

      val files: List[FileChange] = gitReader.readFilesFromCommit(commitId)

      Then("Have correct files")

      assertThat(files.asJava).containsExactly(FileModify("app/style/default.css"),
                                               FileModify("app/view/calculator.html"),
                                               FileModify("app/view/home.html"))
    }


    scenario("Get files content from commit") {
      Given("Repository and GitReader instance, and commit id")

      val gitReader = new GitReader("c:/TmpRepo/")
      val commitId = "e20b7e4df6d4b7df4816a75981331201317e90e8"

      When("Getting committed files")

      val files: List[FileContent] = gitReader.readFilesContentFromCommit(commitId)

      Then("Have correct files")

      val homeFile = files(2).asInstanceOf[FileContentModify]

      assertThat(new String(homeFile.fromContent)).isEqualTo(
        "<div>\n"+
          "    <div>\n"+
          "        This is home page:\n"+
          "    </div>\n"+
          "    <div>\n"+
          "        You can go to <a href=\"#/calculator\">Calculator</a>.\n"+
          "    </div>\n"+
          "</div>")

      assertThat(new String(homeFile.toContent)).isEqualTo(
        "<div>\n" +
        "    <div>\n" +
        "        This is a home page:\n" +
        "    </div>\n" +
        "    <div>\n" +
        "        You can go to a <a href=\"#/calculator\">Calculator</a>.\n" +
        "    </div>\n" +
        "</div>")





    }

  }

}
