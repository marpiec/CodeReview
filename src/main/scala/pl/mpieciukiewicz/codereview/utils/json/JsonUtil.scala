package pl.mpieciukiewicz.codereview.utils.json

import pl.marpiec.mpjsons.MPJson
import org.joda.time.DateTime
import pl.mpieciukiewicz.codereview.utils.enums.EnumHolder

/**
 * @author Marcin Pieciukiewicz
 */
class JsonUtil {

  MPJson.registerConverter(classOf[DateTime], DateTimeConverter)

  def toJson(entity:Any):String = MPJson.serialize(entity.asInstanceOf[AnyRef])

  def fromJson[T](json: String, clazz: Class[T]):T = MPJson.deserialize(json, clazz).asInstanceOf[T]

}
