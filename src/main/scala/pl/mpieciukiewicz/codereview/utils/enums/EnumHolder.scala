package pl.mpieciukiewicz.codereview.utils.enums

import pl.mpieciukiewicz.codereview.model.constant.ProjectRole

/**
 *
 */
abstract class EnumType {
  val name:String
}

abstract class EnumHolder[T <: EnumType] {

  val values:List[T]

  def valueOf(name: String):T = values.find(_.name == name).get

}
