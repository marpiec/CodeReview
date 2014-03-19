package pl.mpieciukiewicz.codereview.git

import pl.mpieciukiewicz.codereview.vcs.Diff
import scala.util.matching.Regex
import scala.util.matching.Regex.Match
import pl.mpieciukiewicz.codereview.vcs.diff.{RemovedLine, ChangedLine, AddedLine}
import scala.annotation.switch


class GitDiffParser {

  case class ChangeBlock(removeFrom: Int, removeCount: Int, addFrom: Int, addCount: Int)

  def parse(diffLines: Iterator[String]):Diff = {
    val lines = diffLines.dropWhile(!_.startsWith("---"))

    val fromFileName = extractRelativeFileName(lines.next())
    val toFileName = extractRelativeFileName(lines.next())

    var changedLines = List[ChangedLine]()

    var line = lines.next()

    while (lines.hasNext) {
      val (nextLine, changesFromBlock) = parseChangeBlock(line, lines)
      line = nextLine
      changedLines :::= changesFromBlock
    }

    Diff(fromFileName, toFileName, changedLines.reverse)
  }

  private def extractRelativeFileName(fileNameEntryLine: String): String = {
    fileNameEntryLine.substring(6)
  }


  private def parseChangeBlockDescription(line: String): ChangeBlock = {
    val pattern = "@@ -([0-9]+),([0-9]+) \\+([0-9]+),([0-9]+) @@".r
    val found = pattern.findAllMatchIn(line).next()
    ChangeBlock(found.group(1).toInt, found.group(2).toInt, found.group(3).toInt, found.group(4).toInt)
  }

  private def parseChangeBlock(firstLine: String, lines: Iterator[String]):(String, List[ChangedLine]) = {
    val changeBlock = parseChangeBlockDescription(firstLine)
    var addCounter = 0
    var removeCounter = 0
    var firstChar:Char = 0
    var changedLines = List[ChangedLine]()
    var line:String = null

    do {
      line = lines.next()
      firstChar = if(line.nonEmpty) line(0) else ' '

      (firstChar: @switch) match {
        case '+' =>
          changedLines ::= AddedLine(changeBlock.addFrom + addCounter, line.tail)
          addCounter += 1
        case '-' =>
          changedLines ::= RemovedLine(changeBlock.removeFrom + removeCounter, line.tail)
          removeCounter +=1
        case ' ' =>
          addCounter += 1
          removeCounter += 1
        case _ => ()
      }
    } while(firstChar != '@' && lines.hasNext)

    (line, changedLines)

  }

}
