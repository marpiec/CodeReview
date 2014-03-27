package pl.mpieciukiewicz.codereview.vcs.git

import org.joda.time.DateTime

/**
 *
 */
case class GitCommit(id: String, author:String, commiter:String, message: String, time: DateTime) {

}
