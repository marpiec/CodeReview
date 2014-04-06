package pl.mpieciukiewicz.codereview.utils.validation

import java.util.regex.Pattern
import org.apache.commons.lang3.StringUtils

/**
 * ...
 * @author Marcin Pieciukiewicz
 */

object EmailValidator {

  val requiredMessage = "Email is required"
  val errorMessage = "Email is incorrect"

  val emailPattern = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")

  def validate(result: ValidationResult, email: String) {
    if (StringUtils.isBlank(email)) {
      result.addError(requiredMessage)
    } else if (isNotValid(email)) {
      result.addError(errorMessage)
    }
  }

  def isNotValid(email: String): Boolean = {
    !isValid(email)
  }

  def isValid(email: String): Boolean = {
    if (email == null) {
      return false
    }
    val matcher = emailPattern.matcher(email)
    matcher.matches
  }

}
