package pl.mpieciukiewicz.codereview.utils.validation

import org.scalatest.{GivenWhenThen, FeatureSpec}
import org.fest.assertions.api.Assertions._

/**
 * ...
 * @author Marcin Pieciukiewicz
 */

class EmailValidatorSpec extends FeatureSpec with GivenWhenThen {

  feature("Can validate email address") {

    scenario("Multiple emails are tested") {

      assertThat(EmailValidator.isNotValid("")).isTrue
      assertThat(EmailValidator.isNotValid(null)).isTrue
      assertThat(EmailValidator.isValid("marcin@socnet.pl")).isTrue
      assertThat(EmailValidator.isValid("marcin@socnet.mobile")).isTrue
      assertThat(EmailValidator.isValid("marcin-p@1.pl")).isTrue
      assertThat(EmailValidator.isValid("marcin.p@1.pl")).isTrue

      val result = new ValidationResult
      EmailValidator.validate(result, "Marcin@socnet@pl")
      assertThat(result.isInvalid).isTrue

      assertThat(EmailValidator.isNotValid("Marcin@socnet@pl")).isTrue
      assertThat(EmailValidator.isNotValid("Marcin@socnet")).isTrue
      assertThat(EmailValidator.isNotValid(".marcin@socnet.pl")).isTrue
      assertThat(EmailValidator.isNotValid("Marcin..p@socnet.pl")).isTrue
      assertThat(EmailValidator.isNotValid("Marcin.p@.socnet.pl")).isTrue
      assertThat(EmailValidator.isNotValid("   Marcin@socnet")).isTrue
      assertThat(EmailValidator.isNotValid("   Marcin@socnet   ")).isTrue
      assertThat(EmailValidator.isNotValid("dsfgsdfg")).isTrue
      assertThat(EmailValidator.isNotValid("marcin@%*.pl")).isTrue

    }
  }
}
