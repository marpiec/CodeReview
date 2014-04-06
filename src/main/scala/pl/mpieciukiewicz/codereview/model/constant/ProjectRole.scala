package pl.mpieciukiewicz.codereview.model.constant

import pl.mpieciukiewicz.codereview.utils.enums.{SEnum, SEnumObject}


object ProjectRole extends SEnumObject[ProjectRole] {

  val Admin = new ProjectRole("Admin")
  val Developer = new ProjectRole("Developer")

  val values = List(Admin, Developer)

  def getValues = values
}

case class ProjectRole(name: String) extends SEnum[ProjectRole] {
  def getName = name
}




