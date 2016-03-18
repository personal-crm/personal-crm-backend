package tools.scrapers.webpage.models

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import play.api.libs.json.Json
import scala.collection.JavaConversions._

case class Meta(
                 name: String,
                 value: String,
                 attrs: Map[String, String],
                 html: String
               )
object Meta {
  implicit val format = Json.format[Meta]

  def parse(elt: Element): Meta = {
    Meta(
      getAttrs(elt, Seq("name", "property", "itemprop", "http-equiv")),
      getAttrs(elt, Seq("content", "charset")),
      elt.attributes().map { attr => (attr.getKey, attr.getValue) }.toMap,
      elt.toString
    )
  }

  private def getAttrs(elt: Element, names: Seq[String]): String = {
    names.map{ name => elt.attr(name) }.find(value => value != "").getOrElse("")
  }
}

case class MetaInfo(
                     url: Option[String],
                     name: Option[String],
                     title: Option[String],
                     description: Option[String],
                     picture: Option[String],
                     keywords: Seq[String],
                     author: Option[String],
                     twitter: Option[String],
                     metas: Seq[Meta]
                   )

object MetaInfo {
  implicit val format = Json.format[MetaInfo]

  def parse(html: String): MetaInfo = {
    val doc = Jsoup.parse(html)
    val title = doc.select("head title")
    val metas = Seq(Meta("title", title.text(), Map(), title.toString)) ++ doc.select("head meta").map(Meta.parse).toSeq
    MetaInfo(
      getMetas(metas, Seq("og:url", "twitter:url")).map(_.value),
      getMetas(metas, Seq("og:site_name")).map(_.value),
      getMetas(metas, Seq("title", "name", "og:title", "twitter:title")).map(_.value),
      getMetas(metas, Seq("description", "og:description", "twitter:description")).map(_.value),
      getMetas(metas, Seq("image", "og:image", "twitter:image", "twitter:image:src")).map(_.value),
      getMetas(metas, Seq("keywords")).map(_.value.split(",").map(_.trim).toSeq).getOrElse(Seq()),
      getMetas(metas, Seq("author")).map(_.value),
      getMetas(metas, Seq("twitter:site")).map(_.value),
      metas
    )
  }

  private def getMetas(metas: Seq[Meta], names: Seq[String]): Option[Meta] = {
    names
      .find { name => metas.exists(_.name == name) }
      .flatMap { name => metas.find(_.name == name) }
  }
}
