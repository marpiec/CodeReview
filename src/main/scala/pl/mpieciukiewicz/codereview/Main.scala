package pl.mpieciukiewicz.codereview

import java.net.{InetSocketAddress, SocketAddress, URI, ProxySelector}
import java.io.IOException
import pl.mpieciukiewicz.codereview.web.WebServer
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
      defineProxy(config)
    }

    new WebServer(config).start()
  }


  private def defineProxy(config: Configuration) {

    System.setProperty("http.proxyHost", config.proxy.host)
    System.setProperty("http.proxyPort", config.proxy.port.toString)

    // this is required by jGit
    ProxySelector.setDefault(new ProxySelector() {
      override def connectFailed(uri: URI, sa: SocketAddress, ioe: IOException) {
        if (uri == null || sa == null || ioe == null) {
          throw new IllegalArgumentException("Arguments can't be null.")
        }
      }
      override def select(uri: URI): java.util.List[java.net.Proxy] = {
        java.util.Arrays.asList(new java.net.Proxy(java.net.Proxy.Type.HTTP, InetSocketAddress.createUnresolved(config.proxy.host, config.proxy.port)))
      }
    })
  }


}
