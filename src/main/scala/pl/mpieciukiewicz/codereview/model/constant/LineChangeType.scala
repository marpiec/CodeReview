package pl.mpieciukiewicz.codereview.model.constant

import pl.mpieciukiewicz.codereview.utils.enums.{SEnum, SEnumObject}

object LineChangeType extends SEnumObject[LineChangeType] {

  val Add = new LineChangeType("Add")
  val Delete = new LineChangeType("Delete")

  val values = List(Add, Delete)

  def getValues = values
}


case class LineChangeType(name: String) extends SEnum[LineChangeType] {
  def getName = name
}
