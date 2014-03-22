package pl.mpieciukiewicz.codereview.model

import org.joda.time.DateTime

/**
 *
 */
case class CommitComment(id: Int,
                         commitId: Int,
                         userId: Int,
                         time: DateTime,
                         comment: String,
                         responseToId: Option[Int])