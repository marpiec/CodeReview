package pl.mpieciukiewicz.codereview.vcs

/**
 *
 */
abstract class LineChange {
  def added: Boolean
  def delete: Boolean
}

case class LineDeleted(number: Int, content: String) extends LineChange {
  def added = false
  def delete = true
}

case class LineAdded(number: Int, content: String) extends LineChange {
  def added = true
  def delete = false
}
