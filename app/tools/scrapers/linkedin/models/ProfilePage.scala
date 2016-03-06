package tools.scrapers.linkedin.models

import common.StringUtils._
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import play.api.libs.json.Json
import tools.scrapers.JsoupUtils._

import scala.collection.JavaConversions._


case class ProfilePageItem(
                            name: String,
                            link: Option[String],
                            logo: Option[String]
                          )

object ProfilePageItem {
  implicit val format = Json.format[ProfilePageItem]
  val empty = ProfilePageItem("", None, None)

  def parse(elt: Element): ProfilePageItem = {
    val link = elt.select("a").attr("href")
    ProfilePageItem(elt.text().replace(",", ""), if (link.length > 0) Some(link) else None, None)
  }

  def parsePositionCompany(eltOpt: Option[Element]): Option[ProfilePageItem] = eltOpt.map(elt => parsePositionCompany(elt))

  def parsePositionCompany(elt: Element): ProfilePageItem = {
    ProfilePageItem(
      elt.select(".item-subtitle").text(),
      elt.select(".item-subtitle a").attr("href").toOpt,
      elt.select(".logo img").attr("data-delayed-url").toOpt)
  }

  def parsePositionSchool(eltOpt: Option[Element]): Option[ProfilePageItem] = eltOpt.map(elt => parsePositionSchool(elt))

  def parsePositionSchool(elt: Element): ProfilePageItem = {
    ProfilePageItem(
      elt.select(".item-title").text(),
      elt.select(".item-title a").attr("href").toOpt,
      elt.select(".logo img").attr("data-delayed-url").toOpt)
  }

  def parseGroup(eltOpt: Option[Element]) = parsePositionSchool(eltOpt)

  def parseGroup(elt: Element) = parsePositionSchool(elt)
}

case class ProfilePageRelated(
                               avatar: String,
                               profileUrl: String,
                               name: String,
                               title: String
                             )

object ProfilePageRelated {
  implicit val format = Json.format[ProfilePageRelated]

  def parse(elt: Element): ProfilePageRelated = {
    val title = elt.select(".item-title a")
    ProfilePageRelated(
      elt.select("img").attr("data-delayed-url"),
      title.attr("href"),
      title.text(),
      elt.select(".headline").text())
  }
}

case class ProfilePageSummary(
                               avatar: String,
                               name: String,
                               title: String,
                               location: String,
                               industry: String,
                               currentPositions: List[ProfilePageItem],
                               pastPositions: List[ProfilePageItem],
                               educations: List[ProfilePageItem],
                               websites: List[ProfilePageItem],
                               recommandationCount: Int,
                               connectionCount: Int
                             )

object ProfilePageSummary {
  implicit val format = Json.format[ProfilePageSummary]
  val empty = ProfilePageSummary("", "", "", "", "", List(), List(), List(), List(), 0, 0)

  def parse(eltOpt: Option[Element]): Option[ProfilePageSummary] = eltOpt.map(elt => parse(elt))

  def parse(elt: Element): ProfilePageSummary = {
    val demographics = elt.select("#demographics dt").map(_.text()).zip(elt.select("#demographics dd").map(_.text())).toMap
    ProfilePageSummary(
      elt.select(".profile-picture img").attr("data-delayed-url"),
      elt.select("#name").text(),
      elt.select(".headline").text(),
      demographics.getOrElse("Location", ""),
      demographics.getOrElse("Industry", ""),
      elt.select(".extra-info [data-section=currentPositions] li").map(ProfilePageItem.parse).toList,
      elt.select(".extra-info [data-section=pastPositions] li").map(ProfilePageItem.parse).toList,
      elt.select(".extra-info [data-section=educations] li").map(ProfilePageItem.parse).toList,
      elt.select(".extra-info [data-section=websites] li").map(ProfilePageItem.parse).toList,
      elt.select(".extra-info tr td strong").firstOpt().flatMap(_.text().toIntOpt).getOrElse(0),
      elt.select(".member-connections strong").text().replace("+", "").toIntOrElse(-1)
    )
  }

}

case class ProfilePagePosition(
                                isCurrent: Boolean,
                                company: ProfilePageItem,
                                position: String,
                                start: String,
                                end: Option[String],
                                location: Option[String],
                                descriptionHtml: String
                              )

object ProfilePagePosition {
  implicit val format = Json.format[ProfilePagePosition]

  def parse(elt: Element): ProfilePagePosition = {
    val dates = elt.select(".date-range time").map(_.text())
    ProfilePagePosition(
      elt.attr("data-section") == "currentPositions",
      ProfilePageItem.parsePositionCompany(elt.select("header").firstOpt()).getOrElse(ProfilePageItem.empty),
      elt.select("header .item-title").text(),
      dates.headOption.getOrElse(""),
      dates.drop(1).headOption,
      elt.select(".location").text().toOpt,
      elt.select(".description").html()
    )
  }
}

case class ProfilePageEducation(
                                 isCurrent: Boolean,
                                 school: ProfilePageItem,
                                 position: String,
                                 start: String,
                                 end: Option[String],
                                 location: Option[String],
                                 descriptionHtml: String
                               )

object ProfilePageEducation {
  implicit val format = Json.format[ProfilePageEducation]

  def parse(elt: Element): ProfilePageEducation = {
    val dates = elt.select(".date-range time").map(_.text())
    ProfilePageEducation(
      elt.attr("data-section") == "currentPositions",
      ProfilePageItem.parsePositionSchool(elt.select("header").firstOpt()).getOrElse(ProfilePageItem.empty),
      elt.select("header .item-subtitle").text(),
      dates.headOption.getOrElse(""),
      dates.drop(1).headOption,
      elt.select(".location").text().toOpt,
      elt.select(".description").html()
    )
  }
}

case class ProfilePage(
                        url: String,
                        summary: ProfilePageSummary,
                        resumeHtml: String,
                        positions: List[ProfilePagePosition],
                        eduction: List[ProfilePageEducation],
                        interests: List[String],
                        skills: List[String],
                        lastRecommendations: List[String],
                        groups: List[ProfilePageItem],
                        relatedProfiles: List[ProfilePageRelated]
                      )

object ProfilePage {
  implicit val format = Json.format[ProfilePage]

  def parse(html: String): ProfilePage = {
    val doc = Jsoup.parse(html)
    ProfilePage(
      doc.select("head link[rel=canonical]").attr("href"),
      ProfilePageSummary.parse(doc.select(".profile-card").firstOpt()).getOrElse(ProfilePageSummary.empty),
      doc.select("#summary .description").html(),
      doc.select("#experience .positions .position").map(ProfilePagePosition.parse).toList,
      doc.select("#education .schools .school").map(ProfilePageEducation.parse).toList,
      doc.select("#interests .pills li").map(_.text()).toList,
      doc.select("#skills .pills li").map(_.text()).toList,
      doc.select("#recommendations .recommendation").map(_.text()).toList,
      doc.select("#groups .group").map(ProfilePageItem.parseGroup).toList.filter(_.name.length > 0),
      doc.select(".browse-map .profile-card").map(ProfilePageRelated.parse).toList
    )
  }
}
