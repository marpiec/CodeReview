package pl.mpieciukiewicz.codereview.database.engine

import pl.mpieciukiewicz.codereview.utils.json.JsonUtil

class DocumentDataStorage(val dba: DatabaseAccessor, jsonUtil: JsonUtil) {

  import dba._



  def initDatabaseStructure() {
    updateNoParams("CREATE TABLE IF NOT EXISTS entity (id INT NOT NULL PRIMARY KEY, type VARCHAR(255), object CLOB)")
    updateNoParams("CREATE SEQUENCE IF NOT EXISTS entity_seq")
  }

  def saveNewEntity[T](entity: T {def id:Option[Int]}): T = {
    update("INSERT INTO entity (id, type, object) VALUES (?, ?, ?)") { preparedStatement =>
      preparedStatement.setInt(1, entity.id.get)
      preparedStatement.setString(2, entity.getClass.getName)
      preparedStatement.setString(3, jsonUtil.toJson(entity))
    }
    entity
  }

  def saveNewEntityNew[T](entity: T {def id:Int}): T = {
    update("INSERT INTO entity (id, type, object) VALUES (?, ?, ?)") { preparedStatement =>
      preparedStatement.setInt(1, entity.id)
      preparedStatement.setString(2, entity.getClass.getName)
      preparedStatement.setString(3, jsonUtil.toJson(entity))
    }
    entity
  }

  def saveAllNewEntities[T](entities: List[T {def id:Option[Int]}]): List[T] = {

    prepareStatement("INSERT INTO entity (id, type, object) VALUES (?, ?, ?)") { preparedStatement =>
      for(entity <- entities) {
        preparedStatement.setInt(1, entity.id.get)
        preparedStatement.setString(2, entity.getClass.getName)
        preparedStatement.setString(3, jsonUtil.toJson(entity))
        preparedStatement.execute()
      }
    }
    entities
  }

  def saveAllNewEntitiesNew[T](entities: List[T {def id:Int}]): List[T] = {

    prepareStatement("INSERT INTO entity (id, type, object) VALUES (?, ?, ?)") { preparedStatement =>
      for(entity <- entities) {
        preparedStatement.setInt(1, entity.id)
        preparedStatement.setString(2, entity.getClass.getName)
        preparedStatement.setString(3, jsonUtil.toJson(entity))
        preparedStatement.execute()
      }
    }
    entities
  }

  def loadNewId():Int = {
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
