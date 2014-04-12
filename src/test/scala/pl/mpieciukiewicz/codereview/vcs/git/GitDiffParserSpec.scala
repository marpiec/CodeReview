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
