package pl.mpieciukiewicz.codereview.utils

import org.scalatest.{GivenWhenThen, FeatureSpec}
import org.fest.assertions.api.Assertions._

/**
 * @author Marcin Pieciukiewicz
 */

class TemplateUtilSpec extends FeatureSpec with GivenWhenThen {

  feature("Can create final string from template and data") {

    scenario("Converting template to text") {

      Given("Template and model")
      val template = "<html><p>Hello #user#.</p><p>Please visit our site: #url#</p>" +
        "<p>Don't forget about #elem1# #elem2# i #elem3#</html>"

      val model = Map[String, String]("user" -> "Marcin",
                                      "url" -> "www.mpieciukiewicz.pl",
                                      "elem1" -> "element1",
                                      "elem2" -> "element2",
                                      "elem3" -> "element3" )

      When("Template are processed ")
      val mail1 = TemplateUtil.fillTemplate(template, model)
      val mail2 = TemplateUtil.fillTemplate(template, model)

      Then("Result is correct, and two results from the same templates and data are same")
      assertThat(mail1).isEqualTo("<html><p>Hello Marcin.</p><p>Please visit our site: www.mpieciukiewicz.pl</p>" +
        "<p>Don't forget about element1 element2 i element3</html>")

      assertThat(mail1).isEqualTo(mail2)
    }

  }


}
