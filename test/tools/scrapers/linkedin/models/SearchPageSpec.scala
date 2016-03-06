package tools.scrapers.linkedin.models

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

import scala.io.Source

class SearchPageSpec extends PlaySpec {
  "LinkedIn SearchPage scraper" should {
    "parse html response" in {
      val html = Source.fromFile("test/tools/scrapers/linkedin/models/SearchPage.html").mkString
      val json = Source.fromFile("test/tools/scrapers/linkedin/models/SearchPage.json").mkString
      SearchPage.parse(html) === Json.parse(json).as[SearchPage]
    }
    // TODO : search when there is only one result (ex: loic knuchel)
  }
}
