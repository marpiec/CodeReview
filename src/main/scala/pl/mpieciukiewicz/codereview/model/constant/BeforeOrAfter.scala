package pl.mpieciukiewicz.codereview.model.constant

import pl.mpieciukiewicz.codereview.utils.enums.{SEnum, SEnumObject}

object BeforeOrAfter extends SEnumObject[BeforeOrAfter] {

  val Before = new BeforeOrAfter("Before")
  val After = new BeforeOrAfter("After")

  val values = List(Before, After)

  def getValues = values
}

case class BeforeOrAfter(name: String) extends SEnum[BeforeOrAfter] {
  def getName = name
}
