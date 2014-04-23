package pl.mpieciukiewicz.codereview.utils

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.lang3.{StringUtils, RandomStringUtils}

class PasswordUtil(systemSalt: String) {

  final val randomPasswordLength = 12
  final val saltLength = 24
  val HASH_COMPUTATION_TIMES = 4000 //As recommended by OWASP

  def generateRandomSalt = RandomStringUtils.randomAlphanumeric(saltLength)

  def generateRandomPassword = RandomStringUtils.randomAlphanumeric(randomPasswordLength)

  def hashPassword(password: String, salt: String): String = {

    if (StringUtils.isBlank(systemSalt)) {
      throw new IllegalStateException("System salt is not defined!")
    }

    if (StringUtils.isBlank(password)) {
      throw new IllegalArgumentException("Password mustn't be empty")
    }

    if (StringUtils.isBlank(salt)) {
      throw new IllegalArgumentException("Salt mustn't be empty")
    }

    var digest = password.trim
    for (p <- 0 until HASH_COMPUTATION_TIMES) {
      digest = DigestUtils.sha512Hex(systemSalt + digest + salt)
    }
    digest
  }

}
