package pl.mpieciukiewicz.codereview.model

import org.joda.time.DateTime

/**
 *
 */
case class Repository(id: Int,
                      projectId: Int,
                      name: String,
                      remoteUrl: String,
                      localDir: String,
                      lastUpdateTime: DateTime)
