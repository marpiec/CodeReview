package pl.mpieciukiewicz.codereview.system

import pl.mpieciukiewicz.codereview.database.{LineCommentStorage, FileCommentStorage, CommitCommentStorage}
import pl.mpieciukiewicz.codereview.utils.clock.Clock
import pl.mpieciukiewicz.codereview.model.client.CommitComments

class CommentManager(commitCommentStorage :CommitCommentStorage,
                     fileCommentStorage :FileCommentStorage,
                     lineCommentStorage :LineCommentStorage,
                     clock: Clock) {

  def addCommitComment(userId: Int, comment: String) {

  }


  def addFileComment(userId: Int, comment: String) {

  }

  def addLineComment(userId: Int, comment: String) {

  }

  def loadCommentsForCommit(commitId: Int): CommitComments = {
    null
  }
}
