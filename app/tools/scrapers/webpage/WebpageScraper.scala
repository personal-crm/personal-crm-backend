package tools.scrapers.webpage

import javax.inject.Inject

import play.api.libs.ws.WSClient
import play.api.mvc._
import tools.scrapers.ScraperUtils
import tools.scrapers.linkedin.models.{ProfilePage, SearchPage}
import tools.scrapers.webpage.models.MetaInfo

class WebpageScraper @Inject()(ws: WSClient) extends Controller {
  def meta(url: String) = Action.async {
    ScraperUtils.scrapeHtml(ws, url)(MetaInfo.parse)
  }
}
