package tools.scrapers.linkedin.models

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import play.api.libs.json.Json

import scala.collection.JavaConversions._

case class SearchPageSearch(
                             firstName: String,
                             lastName: String)

object SearchPageSearch {
  implicit val format = Json.format[SearchPageSearch]

  def parse(elt: Element): SearchPageSearch = {
    SearchPageSearch(
      elt.select("input#firstName").attr("value"),
      elt.select("input#lastName").attr("value"))
  }
}

case class SearchPagePosition(
                               position: String,
                               company: String)

object SearchPagePosition {
  implicit val format = Json.format[SearchPagePosition]
}

case class SearchPageResult(
                             avatar: String,
                             profileUrl: String,
                             name: String,
                             title: String,
                             location: String,
                             industry: String,
                             positions: List[SearchPagePosition],
                             pastPositions: List[SearchPagePosition],
                             education: List[String],
                             summary: String)

object SearchPageResult {
  implicit val format = Json.format[SearchPageResult]

  def parse(elt: Element): SearchPageResult = {
    val title = elt.select(".content h3 a")
    val demographics = elt.select(".basic dt").map(_.text()).zip(elt.select(".basic dd").map(_.text())).toMap
    val experiences = elt.select(".content .expanded tr").map { xp => (xp.select("th").text(), parsePositions(xp.select("td").text())) }.toMap
    SearchPageResult(
      elt.select(".profile-img img").attr("src"),
      title.attr("href"),
      title.text(),
      elt.select(".content .headline").text(),
      demographics.getOrElse("Location", ""),
      demographics.getOrElse("Industry", ""),
      experiences.getOrElse("Current", List()),
      experiences.getOrElse("Past", List()),
      experiences.getOrElse("Education", List()).map(_.position),
      experiences.getOrElse("Summary", List()).headOption.map(_.position).getOrElse(""))
  }

  private def parsePositions(positions: String): List[SearchPagePosition] = positions.split(", ").map(parsePosition).toList

  private def parsePosition(position: String): SearchPagePosition = {
    val values = position.replace(",...", "").split(" at ").toList
    SearchPagePosition(values(0), if (values.size > 1) values(1) else "")
  }
}

case class SearchPage(
                       search: SearchPageSearch,
                       results: List[SearchPageResult])

object SearchPage {
  implicit val format = Json.format[SearchPage]

  def parse(html: String): SearchPage = {
    val doc = Jsoup.parse(html)
    if (doc.select(".primary-section").size() > 0) {
      SearchPage(
        SearchPageSearch.parse(doc.select(".search-form-container").first()),
        doc.select(".primary-section ul.content > li").map(SearchPageResult.parse).toList)
    } else { // when there is only one result, linkedin sends directly the profile page
      val profile = ProfilePage.parse(html)
      SearchPage(
        SearchPageSearch("", ""),
        List(
          SearchPageResult(
            profile.summary.avatar,
            profile.url,
            profile.summary.name,
            profile.summary.title,
            profile.summary.location,
            profile.summary.industry,
            profile.positions.filter(_.isCurrent).map { position => SearchPagePosition(position.position, position.company.name) },
            profile.positions.filter(!_.isCurrent).map { position => SearchPagePosition(position.position, position.company.name) },
            profile.eduction.map(_.school.name),
            profile.resumeHtml)
        )
      )
    }
  }
}
