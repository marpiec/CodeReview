package pl.mpieciukiewicz.codereview.vcs

/**
 *
 */
abstract class FileContent

case class FileContentAdd(content: Array[Byte]) extends FileContent
case class FileContentModify(fromContent: Array[Byte], toContent: Array[Byte]) extends FileContent
case class FileContentDelete(oldContent: Array[Byte]) extends FileContent
case class FileContentRename(fromContent: Array[Byte], toContent: Array[Byte]) extends FileContent
case class FileContentCopy(fromContent: Array[Byte], toContent: Array[Byte]) extends FileContent