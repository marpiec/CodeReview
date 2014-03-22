package pl.mpieciukiewicz.codereview.model.constant

class FileChangeType extends Enumeration {
  type FileChangeType = Value

  val Add = Value("Add")
  val Modify = Value("Modify")
  val Delete = Value("Delete")
  val Rename = Value("Rename")
  val Copy = Value("Copy")

}
