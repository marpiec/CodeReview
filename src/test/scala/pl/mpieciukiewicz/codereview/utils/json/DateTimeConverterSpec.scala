package pl.mpieciukiewicz.codereview.utils.json

import org.scalatest._
import org.fest.assertions.api.Assertions._
import org.joda.time.DateTime
import pl.marpiec.mpjsons.impl.StringIterator

class DateTimeConverterSpec extends FeatureSpecLike with GivenWhenThen {

  feature("Ability to serialize and deserialize DateTime to Json") {

    scenario("Serialization of DateTime") {
      Given("Some date as DateTime and empty StringBuilder")
      val date = new DateTime(2012,12,10,5,30,0,0)
      val jsonBuilder = new StringBuilder

      When("Date is serialized")
      DateTimeConverter.serialize(date, jsonBuilder)

      Then("Builder contains proper json representation of date")
      assertThat(jsonBuilder.toString).isEqualTo("\"2012-12-10 05:30:00:000\"")
    }

    scenario("Deserialization of DateTime") {
      Given("Some date as String and empty StringBuilder")
      val date = "\"2012-12-10 05:30:00:000\"]"

      When("Date is deserialized")
      val dateSerialized = DateTimeConverter.deserialize(new StringIterator(date), classOf[DateTime], null)

      Then("Builder contains proper json representation of date")
      assertThat(dateSerialized).isEqualTo(new DateTime(2012,12,10,5,30,0))
    }

  }

}
