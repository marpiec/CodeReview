package pl.mpieciukiewicz.codereview.model.constant

import pl.mpieciukiewicz.codereview.utils.enums.{EnumHolder, EnumType}


final case class ProjectRole(name: String) extends EnumType

object ProjectRole extends EnumHolder[ProjectRole] {

  val Admin = new ProjectRole("Admin")
  val Developer = new ProjectRole("Developer")

  val values = List[ProjectRole](Admin, Developer)
}







