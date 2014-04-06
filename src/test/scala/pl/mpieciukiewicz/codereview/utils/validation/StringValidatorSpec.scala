package pl.mpieciukiewicz.codereview.utils.validation

import org.scalatest.{GivenWhenThen, FeatureSpec}
import org.fest.assertions.api.Assertions._

/**
 * @author Marcin Pieciukiewicz
 */

class StringValidatorSpec extends FeatureSpec with GivenWhenThen {
  feature("Can validate emails against given lengths") {

    scenario("Multiple validations") {
      assertThat(StringValidator.isValid("abcde", 1, 5)).isTrue

      assertThat(StringValidator.isNotValid("abcde", 6, 10)).isTrue
      assertThat(StringValidator.isNotValid(null, 1, 10)).isTrue
      assertThat(StringValidator.isNotValid("", 1, 10)).isTrue
      assertThat(StringValidator.isNotValid("    ", 1, 10)).isTrue

      val result = new ValidationResult
      StringValidator.validate(result, "    ", 1, 10, "Incorrect value")
      assertThat(result.isInvalid).isTrue

      try {
        StringValidator.isValid("", -1, 10)
        fail()
      } catch {
        case e:IllegalArgumentException => ()
      }

      try {
        StringValidator.isValid("", 10, 4)
        fail()
      } catch {
        case e:IllegalArgumentException => ()
      }
    }

  }

}
