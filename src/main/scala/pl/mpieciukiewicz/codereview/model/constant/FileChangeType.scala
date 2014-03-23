package pl.mpieciukiewicz.codereview.model.constant

import pl.mpieciukiewicz.codereview.utils.enums.{EnumType, EnumHolder}

final case class FileChangeType(name: String) extends EnumType

object FileChangeType extends EnumHolder[FileChangeType] {

  val Add = new FileChangeType("Add")
  val Modify = new FileChangeType("Modify")
  val Delete = new FileChangeType("Delete")
  val Rename = new FileChangeType("Rename")
  val Copy = new FileChangeType("Copy")

  val values = List[FileChangeType](Add, Modify, Delete, Rename, Copy)
}
