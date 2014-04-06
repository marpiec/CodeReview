package pl.mpieciukiewicz.codereview.model.constant

import pl.mpieciukiewicz.codereview.utils.enums.{SEnum, SEnumObject}


object SystemRole extends SEnumObject[SystemRole] {

  val Admin = new SystemRole("Admin")
  val User = new SystemRole("User")

  val values = List(Admin, User)

  def getValues = values
}

case class SystemRole(name: String) extends SEnum[SystemRole] {
  def getName = name
}




