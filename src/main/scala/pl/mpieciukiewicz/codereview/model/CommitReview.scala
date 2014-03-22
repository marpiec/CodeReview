package pl.mpieciukiewicz.codereview.model

import org.joda.time.DateTime

/**
 *
 */
case class CommitReview(id: String,
                        commitId: String,
                        reviewerId: String,
                        positive: Boolean,
                        time: DateTime)
