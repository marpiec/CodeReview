package pl.mpieciukiewicz.codereview.database

import pl.mpieciukiewicz.codereview.utils.{JsonUtil, DatabaseAccessor}
import pl.mpieciukiewicz.codereview.model.User

class DocumentDataStorage(val dba: DatabaseAccessor, jsonUtil: JsonUtil) {

  import dba._


  def addUser(user: User): User = {
    saveNewEntity(user.copy(id = loadNewId()))
  }

  def loadAllUsers():List[User] = {
    loadAllEntitiesByType(classOf[User])
  }

  def findUserByName(userName: String):Option[User] = {
    loadAllEntitiesByType(classOf[User]).find(_.name == userName)
  }

  def initDatabaseStructure() {
    updateNoParams("CREATE TABLE IF NOT EXISTS entity (id INT NOT NULL PRIMARY KEY, type VARCHAR(255), object CLOB)")
    updateNoParams("CREATE SEQUENCE IF NOT EXISTS entity_seq")
  }

  private def saveNewEntity[T](entity: T {def id:Int}): T = {
    update("INSERT INTO entity (id, type, object) VALUES (?, ?, ?)") { preparedStatement =>
      preparedStatement.setInt(1, entity.id)
      preparedStatement.setString(2, entity.getClass.getName)
      preparedStatement.setString(3, jsonUtil.toJson(entity))
    }
    entity
  }

  private def loadNewId():Int = {
    selectNoParams("SELECT NEXTVAL('entity_seq')") { resultSet =>
      resultSet.next()
      resultSet.getInt(1)
    }
  }

  def loadAllEntitiesByType[T](clazz: Class[T]):List[T] = {
    select("SELECT object FROM entity WHERE type = ? ORDER BY id") { preparedStatement =>
      preparedStatement.setString(1, clazz.getName)
    } { resultSet =>
      var entities = List[T]()
      while(resultSet.next()) {
        entities ::= jsonUtil.fromJson(resultSet.getString(1), clazz)
      }
      entities.reverse
    }
  }

  
  def close() {
    dba.close()
  }


}
