package pl.mpieciukiewicz.codereview.git

import pl.mpieciukiewicz.codereview.vcs.{ChangedLine, Diff}
import scala.util.matching.Regex
import scala.util.matching.Regex.Match

case class ChangeBlock(removeFrom: Int, removeCount: Int, addFrom: Int, addCount: Int)

class GitDiffParser {
  def parse(diffLines: Iterator[String]):Diff = {
    val lines = diffLines.dropWhile(!_.startsWith("---"))

    val fromFileName = extractRelativeFileName(lines.next())
    val toFileName = extractRelativeFileName(lines.next())

    val changedLines = List[ChangedLine]()

    while (lines.hasNext) {

      val changeBlock = parseChangeBlockDescription(lines.next())



    }


    Diff(fromFileName, toFileName, List())

  }

  private def extractRelativeFileName(fileNameEntryLine: String): String = {
    fileNameEntryLine.substring(6)
  }


  private def parseChangeBlockDescription(line: String): ChangeBlock = {
    val pattern = "@@ -([0-9]+),([0-9]+) \\+([0-9]+),([0-9]+) @@".r
    val found = pattern.findAllMatchIn(line).next()
    ChangeBlock(found.group(1).toInt, found.group(2).toInt, found.group(3).toInt, found.group(4).toInt)
  }

}
