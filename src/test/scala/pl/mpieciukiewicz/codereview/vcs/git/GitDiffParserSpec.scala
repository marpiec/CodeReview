package pl.mpieciukiewicz.codereview.vcs.git

import org.scalatest.{GivenWhenThen, FeatureSpec}
import scala.io.Source
import pl.mpieciukiewicz.codereview.vcs.{VcsLineAdded, VcsLineDeleted}
import org.fest.assertions.api.Assertions._
import collection.JavaConverters._

/**
 *
 */
class GitDiffParserSpec extends FeatureSpec with GivenWhenThen {

  feature("Parsing of git diff file") {

    scenario("can parse diff file header") {
      Given("Some file headers")
      val parser = new GitDiffParser
      val header1 = "@@ -23,8 +23,8 @@"
      val header2 = "@@ -23,8 @@"
      val header3 = "@@ -23,8 +23,8 -21,3 +24,2 @@"
      val header4 = "@@ -23,8 +23 @@"

      When("Parsing header")
      val changeBlock1 = parser.parseChangeBlockDescription(header1)
      val changeBlock2 = parser.parseChangeBlockDescription(header2)
      val changeBlock3 = parser.parseChangeBlockDescription(header3)
      val changeBlock4 = parser.parseChangeBlockDescription(header4)

      Then("Parsed information is correct")
      assertThat(changeBlock1.removed.asJava).containsExactly(ChangeDescription(23, 8))
      assertThat(changeBlock1.added.asJava).containsExactly(ChangeDescription(23, 8))

      assertThat(changeBlock2.removed.asJava).containsExactly(ChangeDescription(23, 8))
      assertThat(changeBlock2.added.asJava).isEmpty()

      assertThat(changeBlock3.removed.asJava).containsExactly(ChangeDescription(23, 8), ChangeDescription(21, 3))
      assertThat(changeBlock3.added.asJava).containsExactly(ChangeDescription(23, 8), ChangeDescription(24, 2))

      assertThat(changeBlock4.removed.asJava).containsExactly(ChangeDescription(23, 8))
      assertThat(changeBlock4.added.asJava).containsExactly(ChangeDescription(0, 23))

    }

    scenario("can parse simple diff file") {
      
      Given("Simple diff file")
      val diff = Source.fromInputStream(this.getClass.getResourceAsStream("/GitDiffParserSpec.diff")).getLines()
      val parser = new GitDiffParser
      
      When("Parsing diff")
      val parsed = parser.parse(diff)

      Then("Has correct content")

      assertThat(parsed.changedLines.asJava).containsExactly(
        VcsLineDeleted(26, "    $scope.valueA = 0;                          //first value for given operation"),
        VcsLineDeleted(27, "    $scope.valueB = 0;                          //second value for given operation"),
        VcsLineAdded(26, "    $scope.valueA = 0;                          //first (left) value that will be used for computation"),
        VcsLineAdded(27, "    $scope.valueB = 0;                          //second (right) value that will be used for computation"),
        VcsLineDeleted(85, "        $scope.displayValue = Math.floor($scope.selectedOperation($scope.valueA, $scope.valueB));"),
        VcsLineDeleted(86, "        $scope.clearValue = true;"),
        VcsLineDeleted(87, "        $scope.valueA = $scope.displayValue;"),
        VcsLineAdded(85, "        if($scope.selectedOperation!=null) {"),
        VcsLineAdded(86, "            $scope.displayValue = Math.floor($scope.selectedOperation($scope.valueA, $scope.valueB));"),
        VcsLineAdded(87, "            $scope.clearValue = true;"),
        VcsLineAdded(88, "            $scope.valueA = $scope.displayValue;"),
        VcsLineAdded(89, "        }"))

    }
    
  }


}
