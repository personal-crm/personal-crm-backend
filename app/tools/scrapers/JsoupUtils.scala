package tools.scrapers

import org.jsoup.nodes.Element
import org.jsoup.select.Elements

object JsoupUtils {

  implicit class ElementsImprovements(val elts: Elements) {
    def firstOpt(): Option[Element] = if (elts.size() > 0) Some(elts.first()) else None
  }

}
