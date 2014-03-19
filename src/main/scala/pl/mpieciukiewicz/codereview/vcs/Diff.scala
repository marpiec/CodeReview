package pl.mpieciukiewicz.codereview.vcs

import pl.mpieciukiewicz.codereview.vcs.diff.ChangedLine


case class Diff(fromFileName: String,
                toFileName: String,
                changedLines: List[ChangedLine])
