package pl.mpieciukiewicz.codereview.vcs.git

import pl.mpieciukiewicz.codereview.vcs.{VcsLineAdded, VcsLineDeleted, VcsLineChange, VcsFileDiff}
import scala.annotation.switch


class GitDiffParser {

  private case class ChangeBlock(removeFrom: Int, removeCount: Int, addFrom: Int, addCount: Int)

  def parse(lines: Iterator[String]):VcsFileDiff = {

    var changedLines = List[VcsLineChange]()

    while (lines.hasNext) {
      changedLines :::= parseChangeBlock(lines)
    }

    VcsFileDiff(changedLines.reverse)
  }


  private def parseChangeBlockDescription(line: String): ChangeBlock = {
    try {
      val pattern = "@@ -([0-9]+),([0-9]+) \\+([0-9]+),([0-9]+) @@".r
      val found = pattern.findAllMatchIn(line).next()
      ChangeBlock(found.group(1).toInt, found.group(2).toInt, found.group(3).toInt, found.group(4).toInt)
    } catch {
      case e: NoSuchElementException =>
        val pattern = "@@ -([0-9]+) \\+([0-9]+) @@".r
        val found = pattern.findAllMatchIn(line).next()
        ChangeBlock(found.group(1).toInt, 1, found.group(2).toInt, 1)
    }
  }

  private def parseChangeBlock(lines: Iterator[String]): List[VcsLineChange] = {
    val headerLine = lines.next()
    if(headerLine == "\\ No newline at end of file") {
      List()
    } else {
      val changeBlock = parseChangeBlockDescription(headerLine)
      var addCounter = 0
      var removeCounter = 0
      var firstChar: Char = 0
      var changedLines = List[VcsLineChange]()
      var line: String = null

      do {
        line = lines.next()
        firstChar = if (line.nonEmpty) line(0) else ' '

        (firstChar: @switch) match {
          case '+' =>
            changedLines ::= VcsLineAdded(changeBlock.addFrom + addCounter, line.tail)
            addCounter += 1
          case '-' =>
            changedLines ::= VcsLineDeleted(changeBlock.removeFrom + removeCounter, line.tail)
            removeCounter += 1
          case ' ' =>
            addCounter += 1
            removeCounter += 1
          case _ => ()
        }
      } while (addCounter < changeBlock.addCount || removeCounter < changeBlock.removeCount)

      changedLines
    }
  }

}
