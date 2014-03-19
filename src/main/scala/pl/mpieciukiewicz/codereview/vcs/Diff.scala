package pl.mpieciukiewicz.codereview.vcs

abstract class ChangedLine {
  def added: Boolean
  def removed: Boolean
}

case class RemovedLine(number: Int, content: String) extends ChangedLine {
  def added = false
  def removed = true
}

case class AddedLine(number: Int, content: String) extends ChangedLine {
  def added = true
  def removed = false
}

case class Diff(fromFileName: String,
                toFileName: String,
                changedLines: List[ChangedLine])
