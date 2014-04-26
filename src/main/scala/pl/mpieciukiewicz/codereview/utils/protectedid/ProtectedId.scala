package pl.mpieciukiewicz.codereview.utils.protectedid

case class ProtectedId(id: String) {
  def realId:Int = IdProtectionUtil.decrypt(id).toInt
}


object ProtectedId {
  def encrypt(id: Int):ProtectedId = ProtectedId(IdProtectionUtil.encrypt(id))
}
