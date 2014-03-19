package pl.mpieciukiewicz.codereview.vcs

/**
 *
 */
abstract class FileChange

case class FileAdd(newPath: String) extends FileChange
case class FileModify(path: String) extends FileChange
case class FileDelete(oldPath: String) extends FileChange
case class FileRename(oldPath: String, newPath: String) extends FileChange
case class FileCopy(Path: String, newPath: String) extends FileChange
