package pl.mpieciukiewicz.codereview.utils

import org.apache.commons.lang3.RandomStringUtils

/**
 *
 */
class RandomGenerator {

  def generateSessionIdentifier: String = {
    RandomStringUtils.randomAlphabetic(32)
  }

  def generateRepoDirectoryName: String = {
    RandomStringUtils.randomAlphabetic(8)
  }

}
