package pl.mpieciukiewicz.codereview.utils.json

import pl.marpiec.mpjsons.MPJson
import org.joda.time.{Instant, LocalDate, DateTime}
import pl.mpieciukiewicz.codereview.utils.enums.SEnum

/**
 * @author Marcin Pieciukiewicz
 */
class JsonUtil {

  MPJson.registerConverter(classOf[DateTime], DateTimeConverter)
  MPJson.registerConverter(classOf[LocalDate], LocalDateConverter)
  MPJson.registerConverter(classOf[Instant], InstantConverter)
  MPJson.registerSuperclassConverter(classOf[SEnum[_]], SEnumConverter)

  def toJson(entity:Any):String = MPJson.serialize(entity.asInstanceOf[AnyRef])

  def fromJson[T](json: String, clazz: Class[T]):T = MPJson.deserialize(json, clazz).asInstanceOf[T]

}
