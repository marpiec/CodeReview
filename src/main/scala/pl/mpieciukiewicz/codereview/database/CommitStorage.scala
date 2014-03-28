package pl.mpieciukiewicz.codereview.database

import pl.mpieciukiewicz.codereview.database.engine.DocumentDataStorage
import pl.mpieciukiewicz.codereview.model.{Project, Commit}


class CommitStorage(val dds: DocumentDataStorage) {
  import dds._

  def add(commit: Commit): Commit = {
    saveNewEntity(commit.copy(id = Some(loadNewId())))
  }

  def addAll(commits: List[Commit]): List[Commit] = {
    val commitsWithId = commits.map(_.copy(id = Some(loadNewId())))
    saveAllNewEntities(commitsWithId)
  }

  def loadAll(): List[Commit] = {
    loadAllEntitiesByType(classOf[Commit])
  }

  def findById(id: Int): Option[Commit] = {
    loadAllEntitiesByType(classOf[Commit]).find(commit => commit.id.get == id)
  }

  def findByRepositoryId(repositoryId: Int):List[Commit] = {
    loadAllEntitiesByType(classOf[Commit]).filter(commit => commit.repositoryId == repositoryId)
  }


}
