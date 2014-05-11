package pl.mpieciukiewicz.codereview.database

import java.sql.PreparedStatement

trait OptionSupport {

  protected def putIntOption(preparedStatement: PreparedStatement, index: Int, valueOption: Option[Int]) {
    valueOption match {
      case Some(value) => preparedStatement.setInt(index, value)
      case None => preparedStatement.setNull(index, java.sql.Types.INTEGER)
    }
  }

  protected def toIntOption(value: Int): Option[Int] = {
    if (value == 0) {
      None
    } else {
      Some(value)
    }
  }

}
