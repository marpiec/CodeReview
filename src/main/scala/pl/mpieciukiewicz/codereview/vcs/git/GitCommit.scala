package pl.mpieciukiewicz.codereview.vcs.git

import org.joda.time.DateTime
import pl.mpieciukiewicz.codereview.model.Commit

/**
 *
 */
case class GitCommit(id: String, author:String, commiter:String, message: String, time: DateTime, branchName: String) {

  def convertToCommit(repositoryId: Int):Commit = {
    Commit(None, repositoryId, time, id, commiter, author, message, branchName)
  }

}
