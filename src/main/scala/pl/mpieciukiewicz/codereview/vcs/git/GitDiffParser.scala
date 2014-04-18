package pl.mpieciukiewicz.codereview.vcs.git

import pl.mpieciukiewicz.codereview.vcs.{VcsLineAdded, VcsLineDeleted, VcsLineChange, VcsFileDiff}
import scala.annotation.switch
import org.apache.commons.lang3.StringUtils._
import scala.util.matching.Regex.Match

case class ChangeDescription(from: Int, count: Int)

case class ChangeBlock(removed: List[ChangeDescription], added: List[ChangeDescription])

class GitDiffParser {

  def parse(lines: Iterator[String]):VcsFileDiff = {

    var changedLines = List[VcsLineChange]()

    while (lines.hasNext) {
      changedLines :::= parseChangeBlock(lines)
    }

    VcsFileDiff(changedLines.reverse)
  }

  def parseChangeBlockDescription(line: String): ChangeBlock = {

    val pattern = "[+-][0-9]+\\,?[0-9]* ".r
    val matches: Iterator[Match] = pattern.findAllMatchIn(line)

    var removed = List[ChangeDescription]()
    var added = List[ChangeDescription]()

    while(matches.hasNext) {
      val matched: Match = matches.next()
      val found = matched.group(0)

      val p = "([+-])([0-9]+)\\,?([0-9]+)?".r
      val f = p.findAllMatchIn(found).next()

      val sign = f.group(1)

      sign match {
        case "-" => if (f.group(3) == null) {
          removed ::= ChangeDescription(0, f.group(2).toInt)
        } else {
          removed ::= ChangeDescription(f.group(2).toInt, f.group(3).toInt)
        }
        case "+" => if (f.group(3) == null) {
          added ::= ChangeDescription(0, f.group(2).toInt)
        } else {
          added ::= ChangeDescription(f.group(2).toInt, f.group(3).toInt)
        }
        case _ => throw new IllegalStateException("Probelm parsing change block header: ["+line+"]")
      }
    }

    ChangeBlock(removed.reverse, added.reverse)
  }

  private def parseChangeBlock(lines: Iterator[String]): List[VcsLineChange] = {
    val headerLine = lines.next()
    if(isBlank(headerLine) || headerLine == "\\ No newline at end of file") {
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
            changedLines ::= VcsLineAdded(changeBlock.added.head.from + addCounter, line.tail)
            addCounter += 1
          case '-' =>
            changedLines ::= VcsLineDeleted(changeBlock.removed.head.from + removeCounter, line.tail)
            removeCounter += 1
          case ' ' =>
            addCounter += 1
            removeCounter += 1
          case _ => ()
        }
      } while (addCounter < changeBlock.added.head.count || removeCounter < changeBlock.removed.head.count)

      changedLines
    }
  }

}
