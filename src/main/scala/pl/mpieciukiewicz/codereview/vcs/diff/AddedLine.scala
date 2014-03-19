package pl.mpieciukiewicz.codereview.vcs.diff

/**
 *
 */
case class AddedLine(number: Int, content: String) extends ChangedLine {
  def added = true
  def removed = false
}
