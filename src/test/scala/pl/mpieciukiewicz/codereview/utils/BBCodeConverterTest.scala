package pl.mpieciukiewicz.codereview.utils

/**
 * @author Marcin Pieciukiewicz
 */

import org.scalatest.{GivenWhenThen, FeatureSpec}
import org.fest.assertions.api.Assertions._

class BBCodeConverterTest extends FeatureSpec with GivenWhenThen {

  feature("Converts BB code into HTML") {

    scenario("Converts BB code into HTML") {
      Given("BB code input")
      val input = "aaa[b]bbbb[/b]cccc[i]xssss[/i]sdsss[u]sdssf[/u]aaaaaa<script>alert</script>"

      When("Input is converted to HTML")
      val output = BBCodeConverter.convert(input)

      Then("Then HTML is correct")
      assertThat(output).isEqualTo("aaa<b>bbbb</b>cccc<i>xssss</i>sdsss<u>sdssf</u>aaaaaa&lt;script&gt;alert&lt;/script&gt;")
    }

  }
}
