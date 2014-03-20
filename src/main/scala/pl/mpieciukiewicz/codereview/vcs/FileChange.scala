package pl.mpieciukiewicz.codereview.vcs

/**
 *
 */
abstract class FileChange {
  def currentName: Option[String]
}

case class FileAdd(newPath: String) extends FileChange {
  override def currentName = Some(newPath)
}
case class FileModify(path: String) extends FileChange {
  override def currentName = Some(path)
}
case class FileDelete(oldPath: String) extends FileChange {
  override def currentName = None
}
case class FileRename(oldPath: String, newPath: String) extends FileChange {
  override def currentName = Some(newPath)
}
case class FileCopy(oldPath: String, newPath: String) extends FileChange {
  override def currentName = Some(newPath)
}
