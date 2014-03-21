package pl.mpieciukiewicz.codereview.utils

import java.sql._
import org.h2.jdbcx.JdbcConnectionPool


class DatabaseAccessor(url: String, user: String, password: String) {

  val connectionPool = JdbcConnectionPool.create(url, user, password)

  def db[T](codeBlock: Connection => T):T = {
    tryWith(connectionPool.getConnection) { connection =>
      codeBlock(connection)
    }
  }

  def update(connection: Connection)(query: String)(codeBlock: PreparedStatement => Unit) {
    tryWith(connection.prepareStatement(query)) { preparedStatement =>
      codeBlock(preparedStatement)
      preparedStatement.execute()
    }
  }

  def withStatement(connection: Connection)(codeBlock: (Statement) => Unit) {
    tryWith(connection.createStatement()) { statement =>
        codeBlock(statement)
    }
  }

  def selectNoParams[T](connection: Connection)(query: String)(codeBlock: ResultSet => T):T = {
    tryWith(connection.prepareStatement(query)) { preparedStatement =>
      tryWith(preparedStatement.executeQuery()) { resultSet =>
        codeBlock(resultSet)
      }
    }
  }

  def select[T](connection: Connection)(query: String)(paramsBlock: PreparedStatement => Unit)(codeBlock: ResultSet => T):T = {
    tryWith(connection.prepareStatement(query)) { preparedStatement =>
      paramsBlock(preparedStatement)
      tryWith(preparedStatement.executeQuery()) { resultSet =>
        codeBlock(resultSet)
      }
    }
  }

  private def tryWith[E <: AutoCloseable, T](closable: E)(codeBlock: (E) => T): T = {
    try {
      codeBlock(closable)
    } finally {
      closable.close()
    }
  }

}
