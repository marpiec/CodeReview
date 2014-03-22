package pl.mpieciukiewicz.codereview.model

import org.joda.time.DateTime

/**
 *
 */
case class LineComment(id: Int,
                       commitId: Int,
                       userId: Int,
                       fileId: Int,
                       after: Boolean, //TODO: indicates if line is in line before change or after,  change it somehow.
                       lineNumber: Int,
                       time: DateTime,
                       comment: String,
                       responseToId: Option[Int])