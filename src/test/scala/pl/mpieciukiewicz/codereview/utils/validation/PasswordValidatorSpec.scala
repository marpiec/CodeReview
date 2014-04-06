package pl.mpieciukiewicz.codereview.utils.validation

import org.scalatest.{GivenWhenThen, FeatureSpec}
import org.fest.assertions.api.Assertions._

/**
 * @author Marcin Pieciukiewicz
 */

class PasswordValidatorSpec extends FeatureSpec with GivenWhenThen {

  feature("Password validation") {
    scenario("Simple password validation") {
      valid("passw")
      invalid("")
      invalid(null)
      invalid("abcd")
    }
  }
  
  private def valid(password:String) {
    val result = new ValidationResult
    PasswordValidator.validate(result, password)
    assertThat(result.isValid).isTrue
    assertThat(result.isInvalid).isFalse
  }

  private def invalid(password:String) {
    val result = new ValidationResult
    PasswordValidator.validate(result, password)
    assertThat(result.isInvalid).isTrue
    assertThat(result.isValid).isFalse
  }
  
}
