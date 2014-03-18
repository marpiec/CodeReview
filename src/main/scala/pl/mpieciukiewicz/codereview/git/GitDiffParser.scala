package pl.mpieciukiewicz.codereview.git

import pl.mpieciukiewicz.codereview.vcs.Diff

/**
 *
 */
class GitDiffParser {
  def parse(diffLines: Iterator[String]):Diff = {
    val lines = diffLines.dropWhile(!_.startsWith("---"))

    val fromFileName = extractRelativeFileName(lines.next())
    val toFileName = extractRelativeFileName(lines.next())

    Diff(fromFileName, toFileName)

  }

  private def extractRelativeFileName(fileNameEntryLine: String): String = {
    fileNameEntryLine.substring(6)
  }

}
