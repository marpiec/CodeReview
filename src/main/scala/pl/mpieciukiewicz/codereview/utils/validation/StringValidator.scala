package pl.mpieciukiewicz.codereview.utils.validation

import org.apache.commons.lang3.StringUtils


/**
 * @author Marcin Pieciukiewicz
 */

object StringValidator {

  val requiredMessage = "That value is required"

  def validate(result: ValidationResult, value: String, minLength: Int, maxLength: Int,
               requiredMessageAlt:String, errorMessage: String) {
    if (StringUtils.isBlank(value)) {
      result.addError(requiredMessageAlt)
    } else if (isNotValid(value, minLength, maxLength)) {
      result.addError(errorMessage)
    }
  }

  def validate(result: ValidationResult, value: String, minLength: Int, maxLength: Int, errorMessage: String) {
    validate(result, value, minLength, maxLength, requiredMessage, errorMessage)
  }

  def isNotValid(value: String, minLength: Int, maxLength: Int): Boolean = {
    !isValid(value, minLength, maxLength)
  }

  def isValid(value: String, minLength: Int, maxLength: Int): Boolean = {

    if (minLength < 0 || maxLength < 0 || maxLength < minLength) {
      throw new IllegalArgumentException("Incorrect lenghts, min=" + minLength + ", max=" + maxLength)
    }

    if (value == null) {
      return false
    }
    val trimmed = value.trim
    trimmed.size >= minLength && trimmed.size <= maxLength
  }
}
