package tools.scrapers.linkedin.models

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

import scala.io.Source

class ProfilePageSpec extends PlaySpec {
  "LinkedIn ProfilePage scraper" should {
    "parse html response" in {
      val html = Source.fromFile("test/tools/scrapers/linkedin/models/ProfilePage.html").mkString
      val json = Source.fromFile("test/tools/scrapers/linkedin/models/ProfilePage.json").mkString
      ProfilePage.parse(html) === Json.parse(json).as[ProfilePage]
    }
  }
}
