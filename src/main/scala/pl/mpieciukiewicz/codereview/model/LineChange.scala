package pl.mpieciukiewicz.codereview.model

import pl.mpieciukiewicz.codereview.model.constant.LineChangeType

case class LineChange(number: Int, content: String, changeType: LineChangeType)
