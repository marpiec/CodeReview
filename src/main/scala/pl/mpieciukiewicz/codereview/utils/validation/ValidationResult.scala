package pl.mpieciukiewicz.codereview.utils.validation

import org.apache.commons.lang3.StringUtils


/**
 * @author Marcin Pieciukiewicz
 */

class ValidationResult {
  var errors: List[String] = List()

  def addError(error: String) {
    errors = error :: errors
  }

  def isValid = errors.isEmpty

  def isInvalid = errors.nonEmpty
  
  def errorsAsFormattedString:String = {
    StringUtils.join(errors.reverse, ", ")
  }
}
