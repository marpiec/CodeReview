package pl.mpieciukiewicz.codereview.vcs

/**
 *
 */
abstract class VcsLineChange(val added: Boolean, val deleted: Boolean)

case class VcsLineDeleted(number: Int, content: String) extends VcsLineChange(false, true)
case class VcsLineAdded(number: Int, content: String) extends VcsLineChange(true, false)
