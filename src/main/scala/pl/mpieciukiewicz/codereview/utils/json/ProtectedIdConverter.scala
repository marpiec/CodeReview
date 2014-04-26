package pl.mpieciukiewicz.codereview.utils.json

import pl.marpiec.mpjsons.JsonTypeConverter
import pl.mpieciukiewicz.codereview.utils.protectedid.ProtectedId
import pl.marpiec.mpjsons.impl.StringIterator
import java.lang.reflect.Field
import pl.marpiec.mpjsons.impl.deserializer.StringDeserializer

object ProtectedIdConverter extends JsonTypeConverter[ProtectedId] {
  override def serialize(obj: Any, jsonBuilder: StringBuilder) {
    jsonBuilder.append('"')
    jsonBuilder.append(obj.asInstanceOf[ProtectedId].id)
    jsonBuilder.append('"')
  }

  override def deserialize(jsonIterator: StringIterator, clazz: Class[_], field: Field): ProtectedId = {
    val stringValue = StringDeserializer.deserialize(jsonIterator, null, null)
    ProtectedId(stringValue)
  }
}
