package pl.mpieciukiewicz.codereview.git

import pl.mpieciukiewicz.codereview.vcs.{LineAdded, LineDeleted, LineChange, Diff}
import scala.annotation.switch


class GitDiffParser {

  case class ChangeBlock(removeFrom: Int, removeCount: Int, addFrom: Int, addCount: Int)

  def parse(diffLines: Iterator[String]):Diff = {
    val lines = diffLines.dropWhile(!_.startsWith("---"))

    val fromFileName = extractRelativeFileName(lines.next())
    val toFileName = extractRelativeFileName(lines.next())

    var changedLines = List[LineChange]()

    while (lines.hasNext) {
      changedLines :::= parseChangeBlock(lines)
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

  private def parseChangeBlock(lines: Iterator[String]): List[LineChange] = {
    val changeBlock = parseChangeBlockDescription(lines.next())
    var addCounter = 0
    var removeCounter = 0
    var firstChar:Char = 0
    var changedLines = List[LineChange]()
    var line:String = null

    do {
      line = lines.next()
      firstChar = if(line.nonEmpty) line(0) else ' '

      (firstChar: @switch) match {
        case '+' =>
          changedLines ::= LineAdded(changeBlock.addFrom + addCounter, line.tail)
          addCounter += 1
        case '-' =>
          changedLines ::= LineDeleted(changeBlock.removeFrom + removeCounter, line.tail)
          removeCounter +=1
        case ' ' =>
          addCounter += 1
          removeCounter += 1
        case _ => ()
      }
    } while(addCounter < changeBlock.addCount || removeCounter < changeBlock.removeCount)

    changedLines

  }

}
