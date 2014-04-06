package pl.mpieciukiewicz.codereview.utils.validation


/**
 * @author Marcin Pieciukiewicz
 */

object PasswordValidator {

  val requiredMessage = "Password is required"
  val passwordToShortMessage = "Password is to short, min. 5 chars"
  val passwordToLongMessage = "Password is to long, max. 5 chars"


  def validate(result: ValidationResult, password: String) {
    if (password == null || password.isEmpty) {
      result.addError(requiredMessage)
      return
    }

    if (password.size < 5) {
      result.addError(passwordToShortMessage)
      return
    }

    if (password.size > 64) {
      result.addError(passwordToLongMessage)
      return
    }

  }

}
