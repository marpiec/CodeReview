package pl.mpieciukiewicz.codereview.database.engine

import java.sql._
import org.h2.jdbcx.JdbcConnectionPool


class DatabaseAccessor(url: String, user: String, password: String) {

  val connectionPool = JdbcConnectionPool.create(url, user, password)

  def close() {
    connectionPool.dispose()
  }

  def db[T](codeBlock: Connection => T):T = {
    tryWith(connectionPool.getConnection) { connection =>
      codeBlock(connection)
    }
  }

  def updateNoParams(query: String) {
    update(query)({ps => ()})
  }

  def update(query: String)(codeBlock: PreparedStatement => Unit) {
    db { connection =>
      tryWith(connection.prepareStatement(query)) {
        preparedStatement =>
          codeBlock(preparedStatement)
          preparedStatement.execute()
      }
    }
  }

  def selectNoParams[T](query: String)(codeBlock: ResultSet => T):T = {
    select(query)({ps => ()})(codeBlock)
  }

  def select[T](query: String)(paramsBlock: PreparedStatement => Unit)(codeBlock: ResultSet => T):T = {
    db { connection =>
      tryWith(connection.prepareStatement(query)) {
        preparedStatement =>
          paramsBlock(preparedStatement)
          tryWith(preparedStatement.executeQuery()) {
            resultSet =>
              codeBlock(resultSet)
          }
      }
    }
  }

  def prepareStatement(query: String)(codeBlock: PreparedStatement => Unit) {
    db { connection =>
      tryWith(connection.prepareStatement(query)) {
        preparedStatement =>
          codeBlock(preparedStatement)
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
