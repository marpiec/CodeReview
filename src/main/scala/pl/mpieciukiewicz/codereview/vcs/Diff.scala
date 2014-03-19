package pl.mpieciukiewicz.codereview.vcs


case class Diff(fromFileName: String,
                toFileName: String,
                changedLines: List[LineChange])
