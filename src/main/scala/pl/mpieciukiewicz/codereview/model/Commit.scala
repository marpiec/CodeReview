package pl.mpieciukiewicz.codereview.model

import org.joda.time.DateTime

/**
 *
 */
case class Commit(id: Int,
                  repositoryId: Int,
                  time: DateTime,
                  hash: String,
                  commiter: String, //TODO check if this is unique
                  author: String) //TODO check if this is unique)