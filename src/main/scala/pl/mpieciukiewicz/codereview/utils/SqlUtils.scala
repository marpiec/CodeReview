package pl.mpieciukiewicz.codereview.utils

import java.sql._
import com.citi.marsrt.totalrecon.config.Configuration


object SqlUtils {
  def db[T](databaseUrl: String)(codeBlock: (Connection) => T):T = {
    val connection = DriverManager.getConnection(databaseUrl,
      Configuration.instance.database.user, Configuration.instance.database.password)
    val returnValue = codeBlock(connection)
    connection.close()
    returnValue
  }


  def update(connection: Connection)(query: String)(codeBlock: (PreparedStatement) => Unit) {
    val statement = connection.prepareStatement(query)
    codeBlock(statement)
    statement.execute()
    statement.close()
  }

  def withStatement(connection: Connection)(codeBlock: (Statement) => Unit) {
    val statement = connection.createStatement()
    codeBlock(statement)
    statement.close()
  }

  def selectNoParams[T](connection: Connection)(query: String)(codeBlock: (ResultSet) => T):T = {
    val preparedStatement = connection.prepareStatement(query)
    val resultSet = preparedStatement.executeQuery()
    val result = codeBlock(resultSet)
    preparedStatement.close()
    result
  }

  def select[T](connection: Connection)(query: String)(paramsBlock: (PreparedStatement) => Unit)(codeBlock: (ResultSet) => T):T = {
    val preparedStatement = connection.prepareStatement(query)
    paramsBlock(preparedStatement)
    val resultSet = preparedStatement.executeQuery()
    val result = codeBlock(resultSet)
    preparedStatement.close()
    result
  }

}
