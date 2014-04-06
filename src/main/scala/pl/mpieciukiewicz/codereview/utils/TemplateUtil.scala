package pl.mpieciukiewicz.codereview.utils

import util.matching.Regex.Match

/**
 * @author Marcin Pieciukiewicz
 */

trait TemplatePart {
  def apply(stringBuilder: StringBuilder, properties: Map[String, String])
}

class ConstantString(val value: String) extends TemplatePart {
  def apply(stringBuilder: StringBuilder, properties: Map[String, String]) {
    stringBuilder.append(value)
  }
}

class SimpleTagValue(val tagName: String) extends TemplatePart {
  def apply(stringBuilder: StringBuilder, properties: Map[String, String]) {
    stringBuilder.append(properties(tagName))
  }
}


object TemplateUtil {

  val tagRegexp = "#[A-Za-z0-9]+#".r
  var cache: Map[String, List[TemplatePart]] = Map[String, List[TemplatePart]]()

  def fillTemplate(template: String, model: Map[String, String]): String = {

    val templateSequence = getTemplateSequenceFromCache(template)

    val sb = new StringBuilder()

    templateSequence.foreach(templatePart => templatePart.apply(sb, model))

    sb.toString()

  }

  private def getTemplateSequenceFromCache(template: String): List[TemplatePart] = {
    val templateSequenceOption = cache.get(template)
    if (templateSequenceOption.isDefined) {
      templateSequenceOption.get
    } else {
      val templateSequence = createTemplateSequence(template)
      cache += template -> templateSequence
      templateSequence
    }
  }


  private def createTemplateSequence(template: String): List[TemplatePart] = {

    var templateSequence = List[TemplatePart]()
    var matchOption: Option[Match] = None
    var templateTail: CharSequence = template

    do {
      matchOption = tagRegexp.findFirstMatchIn(templateTail)

      if (matchOption.isDefined) {
        val tagMatch = matchOption.get
        if (tagMatch.start > 0) {
          templateSequence ::= new ConstantString(tagMatch.before.toString)
        }
        templateSequence ::= new SimpleTagValue(removeHashes(tagMatch.matched))

        templateTail = tagMatch.after
      }
    } while (matchOption.isDefined)

    if (templateTail.length() > 0) {
      templateSequence ::= new ConstantString(templateTail.toString)
    }

    templateSequence.reverse
  }

  private def removeHashes(value: String): String = {
    value.substring(1, value.length() - 1)
  }
}
