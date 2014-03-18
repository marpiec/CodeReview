package pl.mpieciukiewicz.codereview.git

import org.scalatest.{GivenWhenThen, FeatureSpec}
import scala.io.Source
import org.fest.assertions.api.Assertions.assertThat

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
      assertThat(parsed.fromFileName).isEqualTo("app/js/app.js")
      assertThat(parsed.toFileName).isEqualTo("app/js/app.js")
      
    }
    
  }


}
