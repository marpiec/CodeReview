package pl.mpieciukiewicz.codereview.utils.json

import java.lang.reflect.Field
import org.joda.time.Instant
import pl.marpiec.mpjsons.JsonTypeConverter
import pl.marpiec.mpjsons.impl.StringIterator
import pl.marpiec.mpjsons.impl.deserializer.primitives.LongDeserializer

/**
 * @author Marcin Pieciukiewicz
 */

object InstantConverter extends JsonTypeConverter[Instant] {

  def serialize(obj: Any, jsonBuilder: StringBuilder) {
    jsonBuilder.append(obj.asInstanceOf[Instant].getMillis.toString)
  }

  def deserialize(jsonIterator: StringIterator, clazz: Class[_], field: Field): Instant = {
    val longValue = LongDeserializer.deserialize(jsonIterator, null, null)
    new Instant(longValue)
  }
}
