package pl.mpieciukiewicz.codereview

import java.net.{InetSocketAddress, SocketAddress, URI, ProxySelector}
import java.io.IOException
import pl.mpieciukiewicz.codereview.web.{ProxyConfigurator, WebServer}
import pl.mpieciukiewicz.codereview.ioc.Container
import pl.mpieciukiewicz.codereview.utils.{Configuration, Settings}

/**
 * @author Marcin Pieciukiewicz
 */
object Main {

  def main(args: Array[String]) {

    val config = Container.instance.configuration
    println(config)

    if(config.proxy.enabled) {
      new ProxyConfigurator().configure(config)
    }

    new WebServer(config).start()
  }

}
