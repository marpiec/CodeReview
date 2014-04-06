package pl.mpieciukiewicz.codereview.model.constant

import pl.mpieciukiewicz.codereview.utils.enums.{SEnum, SEnumObject}


object FileChangeType extends SEnumObject[FileChangeType] {

  val Add = new FileChangeType("Add")
  val Modify = new FileChangeType("Modify")
  val Delete = new FileChangeType("Delete")
  val Rename = new FileChangeType("Rename")
  val Copy = new FileChangeType("Copy")

  val values = List(Add, Modify, Delete, Rename, Copy)

  def getValues = values
}

case class FileChangeType(name: String) extends SEnum[FileChangeType] {
  def getName = name
}
