package pl.mpieciukiewicz.codereview.utils.enums

/**
 * @author Marcin Pieciukiewicz
 */

abstract class SEnumObject[T <: SEnum[_]] {

  def getValues: List[SEnum[_]]

  def getByName(name: String): T = {
    for (value <- getValues) {
      if (value.getName == name) {
        return value.asInstanceOf[T]
      }
    }
    throw new IllegalArgumentException("Incorrect name!")
  }
}
