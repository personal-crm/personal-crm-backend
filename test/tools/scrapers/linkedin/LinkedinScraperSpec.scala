package tools.scrapers.linkedin.models

import org.scalatestplus.play.{OneAppPerTest, PlaySpec}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.io.Source

class LinkedinScraperSpec extends PlaySpec with OneAppPerTest {
  "LinkedinScraper" should {

    "perform a profile search" in {
      val json = Source.fromFile("test/tools/scrapers/linkedin/models/SearchPage.json").mkString
      val resultFuture = route(app, FakeRequest(GET, "/api/v1/scrapers/linkedin/search?firstName=pierre&lastName=jean")).get

      status(resultFuture) mustBe OK
      contentType(resultFuture) mustBe Some("application/json")
      contentAsString(resultFuture) must include (json)
    }

    "return search results event if linkedin directly send a profile" in {
      val json = Source.fromFile("test/tools/scrapers/linkedin/models/SearchPageAlone.json").mkString
      val resultFuture = route(app, FakeRequest(GET, "/api/v1/scrapers/linkedin/search?firstName=loic&lastName=knuchel")).get

      status(resultFuture) mustBe OK
      contentType(resultFuture) mustBe Some("application/json")
      contentAsString(resultFuture) must include (json)
    }

    "extract data from a profile" in {
      val json = Source.fromFile("test/tools/scrapers/linkedin/models/ProfilePage.json").mkString
      val resultFuture = route(app, FakeRequest(GET, "/api/v1/scrapers/linkedin/profile?url=https://www.linkedin.com/in/loicknuchel")).get

      status(resultFuture) mustBe OK
      contentType(resultFuture) mustBe Some("application/json")
      contentAsString(resultFuture) must include (json)
    }

  }
}
