package pl.mpieciukiewicz.codereview

import java.net.{InetSocketAddress, SocketAddress, URI, ProxySelector}
import java.{util, net}
import java.io.IOException

/**
 * @author Marcin Pieciukiewicz
 */
object Main {

  def main(args: Array[String]) {

    defineProxy()

    Playground.start()
  }




  def defineProxy() {

    System.setProperty("http.proxyHost", "webproxy.ssmb.com")
    System.setProperty("http.proxyPort", "8080")

    ProxySelector.setDefault(new ProxySelector() {
      override def connectFailed(uri: URI, sa: SocketAddress, ioe: IOException) {
        if (uri == null || sa == null || ioe == null) {
          throw new IllegalArgumentException("Arguments can't be null.")
        }
      }
      override def select(uri: URI): java.util.List[java.net.Proxy] = {
        java.util.Arrays.asList(new java.net.Proxy(java.net.Proxy.Type.HTTP, InetSocketAddress.createUnresolved("webproxy.ssmb.com", 8080)))
      }
    })
  }


}
