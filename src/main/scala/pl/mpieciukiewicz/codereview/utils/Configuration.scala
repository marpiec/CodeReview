package pl.mpieciukiewicz.codereview.utils

import com.typesafe.config._

/**
 *
 */
class Configuration(c: Config) {
  implicit val globalConfig = c

  val webServer = new Settings("webServer") {
    val port = config.getInt("port")
  }

  val proxy = new Settings("proxy") {
    val enabled = config.getBoolean("enabled")
    def host = config.getString("host")
    def port = config.getInt("port")
  }

  val storage = new Settings("storage") {
    val dataDirectory = config.getString("dataDirectory")
  }

  override def toString = {
    globalConfig.root().render(ConfigRenderOptions.concise().setFormatted(true))
  }
}


class Settings(path: String)(implicit globalConfig: Config) {
  implicit protected val config: Config = globalConfig.getConfig(path)
}

