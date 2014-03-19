package pl.mpieciukiewicz.codereview.vcs.diff

/**
 *
 */
case class RemovedLine(number: Int, content: String) extends ChangedLine {
  def added = false
  def removed = true
}
