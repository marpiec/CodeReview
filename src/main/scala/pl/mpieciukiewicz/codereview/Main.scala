package pl.mpieciukiewicz.codereview

import java.net.{InetSocketAddress, SocketAddress, URI, ProxySelector}
import java.{util, net}
import java.io.IOException
import pl.mpieciukiewicz.codereview.web.WebServer
import pl.mpieciukiewicz.codereview.database.DataStorage
import pl.mpieciukiewicz.codereview.model.User

/**
 * @author Marcin Pieciukiewicz
 */
object Main {

  def main(args: Array[String]) {

    defineProxy()

    val storage: DataStorage = new DataStorage("jdbc:h2:data/database", "sa", "sa")
    storage.initDatabaseStructure()

    storage.addUser(User("Marcin", "AAA", "BBB", "m.p@g.pl", "admin"))

    println(storage.findUserByName("Marcin"))

    new WebServer().start()
    //Playground.start()
  }




  private def defineProxy() {

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
