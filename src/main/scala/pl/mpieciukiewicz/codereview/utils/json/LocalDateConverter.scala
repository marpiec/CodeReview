package pl.mpieciukiewicz.codereview.utils.json

import java.lang.reflect.Field
import org.joda.time.format.DateTimeFormat
import org.joda.time.{LocalDate, LocalDateTime}
import pl.marpiec.mpjsons.JsonTypeConverter
import pl.marpiec.mpjsons.impl.StringIterator
import pl.marpiec.mpjsons.impl.deserializer.StringDeserializer

/**
 * @author Marcin Pieciukiewicz
 */

object LocalDateConverter extends JsonTypeConverter[LocalDate] {

  val PATTERN = "yyyy-MM-dd"

  def serialize(obj: Any, jsonBuilder: StringBuilder) {
    jsonBuilder.append('"')
    jsonBuilder.append(obj.asInstanceOf[LocalDate].toString(PATTERN))
    jsonBuilder.append('"')
  }

  def deserialize(jsonIterator: StringIterator, clazz: Class[_], field: Field): LocalDate = {
    val stringValue: String = StringDeserializer.deserialize(jsonIterator, null, null)
    DateTimeFormat.forPattern(PATTERN).parseDateTime(stringValue).toLocalDate
  }
}
