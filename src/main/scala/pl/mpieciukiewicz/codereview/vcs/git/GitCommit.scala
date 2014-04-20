package pl.mpieciukiewicz.codereview.vcs.git

import org.joda.time.DateTime
import pl.mpieciukiewicz.codereview.model.Commit

/**
 *
 */
case class GitCommit(id: String, 
                     author:String, authorEmail: String, 
                     commiter:String, commiterEmail: String, 
                     message: String, time: DateTime) {

  def convertToCommit(repositoryId: Int):Commit = {
    Commit(None, repositoryId, time, id, commiter, commiterEmail, author, authorEmail, message)
  }

}
