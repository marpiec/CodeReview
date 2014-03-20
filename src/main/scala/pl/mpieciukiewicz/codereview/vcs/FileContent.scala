package pl.mpieciukiewicz.codereview.vcs

/**
 *
 */
abstract class FileContent {

}


case class FileContentAdd(content: Array[Byte]) extends FileContent

case class FileContentModify(fromContent: Array[Byte], toContent: Array[Byte]) extends FileContent {

}
case class FileContentDelete(oldContent: Array[Byte]) extends FileContent {

}
case class FileContentRename(oldContent: Array[Byte], newContent: Array[Byte]) extends FileContent {

}
case class FileContentCopy(oldContent: Array[Byte], newContent: Array[Byte]) extends FileContent {

}