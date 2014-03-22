package pl.mpieciukiewicz.codereview.utils

import org.scalatest.{BeforeAndAfter, GivenWhenThen, FeatureSpec, FunSuite}
import org.fest.assertions.api.Assertions._
import org.h2.jdbc.JdbcSQLException

/**
 *
 */
class DatabaseAccessorSpec extends FeatureSpec with GivenWhenThen with BeforeAndAfter {

  var dba:DatabaseAccessor = _

  before {
    dba = new DatabaseAccessor("jdbc:h2:mem:testdb", "sa", "sa")
  }

  after {
    dba.close()
  }

  feature("H2 database support") {

    scenario("Create database structure") {

      Given("Empty database")


      When("Created user table")
      dba.updateNoParams("CREATE TABLE user (id INT NOT NULL PRIMARY KEY, name VARCHAR(255))")

      Then("Creating table again will fail")
      intercept[JdbcSQLException] {
        dba.updateNoParams("CREATE TABLE user (id INT NOT NULL PRIMARY KEY, name VARCHAR(255))")
      }

      Then("Getting all data from table will return no data")
      dba.selectNoParams("SELECT * FROM user") { resultSet =>
        assertThat(resultSet.next()).isFalse
      }

    }

    scenario("Inserting and getting data back") {
      Given("Database with user table")

      dba.updateNoParams("CREATE TABLE user (id INT NOT NULL PRIMARY KEY, name VARCHAR(255))")

      When("Two users are put into database")
      dba.update("INSERT INTO user (id, name) VALUES (?, ?)") { preparedStetement =>
        preparedStetement.setInt(1, 1)
        preparedStetement.setString(2, "Marcin")
      }

      dba.update("INSERT INTO user (id, name) VALUES (?, ?)") { preparedStetement =>
        preparedStetement.setInt(1, 2)
        preparedStetement.setString(2, "John")
      }

      Then("Getting all results from will return 2 correct users")
      dba.selectNoParams("SELECT * FROM user ORDER BY id") { resultSet =>
        assertThat(resultSet.next()).isTrue
        assertThat(resultSet.getInt(1)).isEqualTo(1)
        assertThat(resultSet.getString(2)).isEqualTo("Marcin")

        assertThat(resultSet.next()).isTrue
        assertThat(resultSet.getInt(1)).isEqualTo(2)
        assertThat(resultSet.getString(2)).isEqualTo("John")

        assertThat(resultSet.next()).isFalse
      }

      Then("Getting user by id will return correct user")
      dba.select("SELECT * FROM user WHERE id = ?") { preparedStatement =>
        preparedStatement.setInt(1, 2)
      } { resultSet =>
        assertThat(resultSet.next()).isTrue
        assertThat(resultSet.getInt(1)).isEqualTo(2)
        assertThat(resultSet.getString(2)).isEqualTo("John")
        assertThat(resultSet.next()).isFalse
      }
    }



  }

}
