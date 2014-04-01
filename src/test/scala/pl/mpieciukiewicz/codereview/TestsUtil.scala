package pl.mpieciukiewicz.codereview

import pl.mpieciukiewicz.codereview.utils.RandomGenerator

object TestsUtil {

  val randomGenerator = new RandomGenerator

  def randomMemoryH2Url(): String = {
    "jdbc:h2:mem:" + randomGenerator.generateRepoDirectoryName
  }

}
