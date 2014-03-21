package pl.mpieciukiewicz.codereview.utils

import pl.marpiec.mpjsons.MPJson

/**
 * @author Marcin Pieciukiewicz
 */
object JsonUtil {

  def toJson(entity:Any):String = MPJson.serialize(entity.asInstanceOf[AnyRef])

  def fromJson[T](json: String, clazz: Class[T]):T = MPJson.deserialize(json, clazz).asInstanceOf[T]

}
