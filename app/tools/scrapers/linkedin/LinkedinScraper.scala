package tools.scrapers.linkedin

import javax.inject.Inject

import play.api.libs.ws.WSClient
import play.api.mvc._
import tools.scrapers.ScraperUtils
import tools.scrapers.linkedin.models.{ProfilePage, SearchPage}

class LinkedinScraper @Inject()(ws: WSClient) extends Controller {
  val baseUrl = "https://www.linkedin.com"

  def search(firstName: String, lastName: String) = Action.async {
    ScraperUtils.fetch(ws, s"$baseUrl/pub/dir/?first=$firstName&last=$lastName")(SearchPage.parse)
  }

  def profile(url: String) = Action.async {
    ScraperUtils.fetch(ws, url)(ProfilePage.parse)
  }
}
