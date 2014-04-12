package pl.mpieciukiewicz.codereview.vcs

import pl.mpieciukiewicz.codereview.model.constant.FileChangeType

/**
 *
 */
abstract class VcsFileContent(val changeType: FileChangeType)

case class VcsFileContentAdd(content: String, path: String) extends VcsFileContent(FileChangeType.Add)
case class VcsFileContentModify(fromContent: String, path: String, toContent: String) extends VcsFileContent(FileChangeType.Modify)
case class VcsFileContentDelete(oldContent: String, oldPath: String) extends VcsFileContent(FileChangeType.Delete)
case class VcsFileContentRename(fromContent: String, fromPath: String, toContent: String, toPath: String) extends VcsFileContent(FileChangeType.Rename)
case class VcsFileContentCopy(fromContent: String, fromPath: String, toContent: String, toPath: String) extends VcsFileContent(FileChangeType.Copy)
