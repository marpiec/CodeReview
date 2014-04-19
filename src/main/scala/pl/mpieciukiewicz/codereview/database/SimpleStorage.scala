package pl.mpieciukiewicz.codereview.database

import java.sql.{PreparedStatement, ResultSet}
import pl.mpieciukiewicz.codereview.database.engine.DatabaseAccessor
import pl.mpieciukiewicz.codereview.model.Commit

abstract class SimpleStorage[T](private val da: DatabaseAccessor, private val sm: SequenceManager, tableName:String, columns:List[(String, String)]) {

  private final val SEQUENCE_BUFFER_SIZE = 20

  private lazy val sequenceName = tableName + "_seq"
  private lazy val columnsNames = columns.map(_._1).mkString(", ")
  private lazy val columnsPlaceholders = columns.map(_ => "?").mkString(", ")

  def columnsWithParams = columns.map{case (name, info) => name + " " + info}.mkString(", ")

  import da._

  updateNoParams(
    s"CREATE TABLE IF NOT EXISTS $tableName ($columnsWithParams)"
  )

  updateNoParams(s"CREATE SEQUENCE IF NOT EXISTS $sequenceName INCREMENT $SEQUENCE_BUFFER_SIZE")


  def add(entity: T): T = {
    val newEntity = injectId(entity, sm.nextId(sequenceName))
    update(s"INSERT INTO $tableName ($columnsNames) VALUES ($columnsPlaceholders)") {
      preparedStatement => mapEntityToPrepareStatement(newEntity, preparedStatement)
    }
    newEntity
  }

  def addAll(entities: List[T]): List[T] = {
    entities.map(add)
  }

  protected def loadAll(): List[T] = {
    selectNoParams(s"SELECT $columnsNames FROM $tableName") { resultSet =>
      mapResultSetToEntities(resultSet)
    }
  }

  protected def findMultipleBy(where: String)(paramsBlock: PreparedStatement => Unit): List[T] = {
      select(s"SELECT $columnsNames FROM $tableName WHERE $where") { preparedStatement =>
        paramsBlock(preparedStatement)
      } { resultSet =>
        mapResultSetToEntities(resultSet)
      }
  }

  protected def findSingleBy(where: String)(paramsBlock: PreparedStatement => Unit): Option[T] = {
    select(s"SELECT $columnsNames FROM $tableName WHERE $where") { preparedStatement =>
      paramsBlock(preparedStatement)
    } {
      resultSet =>
        if (resultSet.next()) {
          Some(mapResultToEntity(resultSet))
        } else {
          None
        }
    }
  }


  def findById(id: Int): Option[T] = {
    findSingleBy("id = ?") {
      preparedStatement => preparedStatement.setInt(1, id)
    }
  }

  protected def mapResultSetToEntities(resultSet: ResultSet): List[T] = {
    var entities = List[T]()
    while (resultSet.next()) {
      entities ::= mapResultToEntity(resultSet)
    }
    entities.reverse
  }

  protected def injectId(entity: T, id: Int):T

  protected def mapResultToEntity(resultSet: ResultSet): T

  protected def mapEntityToPrepareStatement(entity: T, preparedStatement: PreparedStatement)
}
