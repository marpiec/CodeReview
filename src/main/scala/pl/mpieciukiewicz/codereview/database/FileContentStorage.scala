package pl.mpieciukiewicz.codereview.database

import pl.mpieciukiewicz.codereview.database.engine.DocumentDataStorage
import pl.mpieciukiewicz.codereview.model.{Commit, Repository, FileContent}

class FileContentStorage(val dds: DocumentDataStorage) {

  import dds._

  def add(fileContent: FileContent): FileContent = {
    saveNewEntityNew(fileContent.copy(id = loadNewId()))
  }

  def addAll(filesContent: List[FileContent]): List[FileContent] = {
    val commitsWithId = filesContent.map(_.copy(id = loadNewId()))
    saveAllNewEntitiesNew(commitsWithId)
  }

  def findByCommit(commitId: Int): List[FileContent] = {
    loadAllEntitiesByType(classOf[FileContent]).filter(fileContent => fileContent.commitId == commitId)
  }

}
