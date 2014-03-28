package pl.mpieciukiewicz.codereview.vcs.git

import org.scalatest.{GivenWhenThen, FeatureSpec}
import pl.mpieciukiewicz.codereview.vcs._
import org.fest.assertions.api.Assertions._
import collection.JavaConverters._
import pl.mpieciukiewicz.codereview.vcs.FileModify
import org.joda.time.DateTime

/**
 *
 */
class GitReaderSpec extends FeatureSpec with GivenWhenThen {

  feature("GIT repository analysis support") {

    scenario("Get recent commits") {
      Given("Repository and GitReader instance")

      val gitReader = new GitReader("c:/TmpRepo/")

      When("Getting 4 Commits")

      val commits: List[GitCommit] = gitReader.readCommits(4)


      Then("Have correct commits")

      assertThat(commits.asJava).containsExactly(
        GitCommit("a6d5e7f8e2e3e9162563bb215bb04bf3a629424a", "Marcin Pieciukiewicz", "Marcin Pieciukiewicz", "Some clean up", new DateTime(1376576166000L)),
        GitCommit("e20b7e4df6d4b7df4816a75981331201317e90e8", "Marcin Pieciukiewicz", "Marcin Pieciukiewicz", "Some clean up", new DateTime(1376554727000L)),
        GitCommit("98fc0f9e817708f0a4f38f630b134251cf076afc", "Marcin Pieciukiewicz", "Marcin Pieciukiewicz", "Sources", new DateTime(1376478826000L)),
        GitCommit("9333a8309c0ec2f6c35d5746861b2b53f8c04955", "Marcin Pieciukiewicz", "Marcin Pieciukiewicz", "Sources", new DateTime(1376478781000L)))

      When("Getting All Commits")

      val allCommits: List[GitCommit] = gitReader.readAllCommits()

      Then("Has correct number of commits")
      assertThat(allCommits.asJava).hasSize(5)

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


    scenario("Get files diff from commit") {
      Given("Repository and GitReader instance, and commit id")

      val gitReader = new GitReader("c:/TmpRepo/")
      val commitId = "e20b7e4df6d4b7df4816a75981331201317e90e8"

      When("Getting committed files")

      val files: List[FileDiff] = gitReader.readFilesDiffFromCommit(commitId)

      Then("Have correct diffs")

      val homeFileDiff = files(2)


      assertThat(homeFileDiff.changedLines.asJava).containsExactly(
        LineDeleted(3,"        This is home page:"),
        LineAdded(3,"        This is a home page:"),
        LineDeleted(6,"        You can go to <a href=\"#/calculator\">Calculator</a>."),
        LineAdded(6,"        You can go to a <a href=\"#/calculator\">Calculator</a>."))


    }

  }



}
