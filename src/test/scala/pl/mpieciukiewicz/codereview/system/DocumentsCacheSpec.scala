package pl.mpieciukiewicz.codereview.system

import org.scalatest._
import org.fest.assertions.api.Assertions._
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class DocumentsCacheSpec extends FeatureSpecLike with GivenWhenThen with BeforeAndAfter {

  feature("Transparent document cache based on String keys and future String values") {

    scenario("Getting value twice from clear cache") {

      Given("Empty document cache and counter")
      val cache = new DocumentsCache
      var callCounter = 0

      When("Getting value twice for the same key")
      val valueGenerator = {
        callCounter += 1
        Future {
          "sampleValue"
        }
      }
      val valueA = Await.result(cache.getOrInsert("sampleKey")(valueGenerator), 5 seconds)
      val valueB = Await.result(cache.getOrInsert("sampleKey")(valueGenerator), 5 seconds)

      Then("Values are equal, but counter was incremented only once")
      assertThat(valueA).isEqualTo(valueB).isEqualTo("sampleValue")
      assertThat(callCounter).isEqualTo(1)


    }

  }

}
