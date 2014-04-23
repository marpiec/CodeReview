package pl.mpieciukiewicz.codereview.model.constant

import pl.mpieciukiewicz.codereview.utils.enums.{SEnum, SEnumObject}


object ProjectRole extends SEnumObject[ProjectRole] {

  val Owner = new ProjectRole("Owner")
  val Admin = new ProjectRole("Admin")
  val Developer = new ProjectRole("Developer")

  val values = List(Owner, Admin, Developer)

  def getValues = values
}

case class ProjectRole(name: String) extends SEnum[ProjectRole] {
  def getName = name
}




