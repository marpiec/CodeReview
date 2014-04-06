package pl.mpieciukiewicz.codereview.utils

/**
 * @author Marcin Pieciukiewicz
 */

object OrderingUtil {

  val STRING_IGNORE_CASE_ORDERING = new Ordering[String] {
    def compare(x: String, y: String) = {
      val comparisonResult = x.toLowerCase.compareTo(y.toLowerCase)
      if (comparisonResult == 0) {
        x.compareTo(y)
      } else {
        comparisonResult
      }
    }
  }
  
}
