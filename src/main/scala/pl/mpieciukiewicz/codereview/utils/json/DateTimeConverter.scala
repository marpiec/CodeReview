package pl.mpieciukiewicz.codereview.utils.json

import java.lang.reflect.Field
import org.joda.time.format.DateTimeFormat
import pl.marpiec.mpjsons.JsonTypeConverter
import pl.marpiec.mpjsons.impl.StringIterator
import pl.marpiec.mpjsons.impl.deserializer.StringDeserializer
import org.joda.time.DateTime

/**
 * @author Marcin Pieciukiewicz
 */

object DateTimeConverter extends JsonTypeConverter[DateTime] {

  val PATTERN = "yyyy-MM-dd HH:mm:ss:SSS"

  def serialize(obj: Any, jsonBuilder: StringBuilder) {
    jsonBuilder.append('"')
    jsonBuilder.append(obj.asInstanceOf[DateTime].toString(PATTERN))
    jsonBuilder.append('"')
  }

  def deserialize(jsonIterator: StringIterator, clazz: Class[_], field: Field): DateTime = {
    val stringValue: String = StringDeserializer.deserialize(jsonIterator, null, null)
    DateTimeFormat.forPattern(PATTERN).parseDateTime(stringValue)
  }
}
