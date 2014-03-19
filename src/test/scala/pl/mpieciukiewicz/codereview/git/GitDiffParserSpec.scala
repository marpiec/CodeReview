package pl.mpieciukiewicz.codereview.git

import org.scalatest.{GivenWhenThen, FeatureSpec}
import scala.io.Source
import pl.mpieciukiewicz.codereview.vcs.diff.{RemovedLine, AddedLine}

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
      assert(parsed.fromFileName == "app/js/app.js")
      assert(parsed.toFileName == "app/js/app.js")

      assert(parsed.changedLines == List(RemovedLine(26, "    $scope.valueA = 0;                          //first value for given operation"),
                                        RemovedLine(27, "    $scope.valueB = 0;                          //second value for given operation"),
                                        AddedLine(26, "    $scope.valueA = 0;                          //first (left) value that will be used for computation"),
                                        AddedLine(27, "    $scope.valueB = 0;                          //second (right) value that will be used for computation"),
                                        RemovedLine(85, "        $scope.displayValue = Math.floor($scope.selectedOperation($scope.valueA, $scope.valueB));"),
                                        RemovedLine(86, "        $scope.clearValue = true;"),
                                        RemovedLine(87, "        $scope.valueA = $scope.displayValue;"),
                                        AddedLine(85, "        if($scope.selectedOperation!=null) {"),
                                        AddedLine(86, "            $scope.displayValue = Math.floor($scope.selectedOperation($scope.valueA, $scope.valueB));"),
                                        AddedLine(87, "            $scope.clearValue = true;"),
                                        AddedLine(88, "            $scope.valueA = $scope.displayValue;"),
                                        AddedLine(89, "        }")
      ))

    }
    
  }


}
