package pl.mpieciukiewicz.codereview.vcs.diff

/**
 *
 */
abstract class ChangedLine {
  def added: Boolean
  def removed: Boolean
}
