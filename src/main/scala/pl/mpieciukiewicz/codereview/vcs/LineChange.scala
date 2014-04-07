package pl.mpieciukiewicz.codereview.vcs

/**
 *
 */
abstract class LineChange(val added: Boolean, val deleted: Boolean)

case class LineDeleted(number: Int, content: String) extends LineChange(false, true)
case class LineAdded(number: Int, content: String) extends LineChange(true, false)
