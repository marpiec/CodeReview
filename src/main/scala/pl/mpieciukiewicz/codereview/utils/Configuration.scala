package pl.mpieciukiewicz.codereview.utils

import com.typesafe.config._
import java.io.InputStreamReader


object Configuration {
  def fromClasspath(fileName: String):Configuration = {
    new Configuration(ConfigFactory.parseReader(new InputStreamReader(classOf[Configuration].getResourceAsStream(fileName))))
  }
}

class Configuration(c: Config) {
  implicit val globalConfig = c

  val webServer = new Settings("webServer") {
    val port = config.getInt("port")
  }

  val proxy = new Settings("proxy") {
    val enabled = config.getBoolean("enabled")
    val host = config.getString("host")
    val port = config.getInt("port")
  }

  val storage = new Settings("storage") {
    val dataDirectory = config.getString("dataDirectory")
  }

  val security = new Settings("security") {
    val systemSalt = config.getString("systemSalt")
  }

  val smtp = new Settings("smtp") {
    val host = config.getString("host")
    val port = config.getInt("port")
    val mailFrom = config.getString("mailFrom")
  }

  override def toString = {
    globalConfig.root().render(ConfigRenderOptions.concise().setFormatted(true))
  }
}


class Settings(path: String)(implicit globalConfig: Config) {
  implicit protected val config: Config = globalConfig.getConfig(path)
}

