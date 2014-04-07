package pl.mpieciukiewicz.codereview.vcs

/**
 *
 */
abstract class FileContent(val changeType: String)

case class FileContentAdd(content: String) extends FileContent("add")
case class FileContentModify(fromContent: String, toContent: String) extends FileContent("modify")
case class FileContentDelete(oldContent: String) extends FileContent("delete")
case class FileContentRename(fromContent: String, toContent: String) extends FileContent("rename")
case class FileContentCopy(fromContent: String, toContent: String) extends FileContent("copy")
