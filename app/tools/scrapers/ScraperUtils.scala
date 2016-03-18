package tools.scrapers

import play.api.libs.json.{JsValue, Json, Writes}
import play.api.libs.ws.{WSResponse, WSClient}
import play.api.mvc.Result
import play.api.mvc.Results.{NotFound, Ok}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

object ScraperUtils {
  def fetchHtml(ws: WSClient, url: String): Future[String] =
    fetch(_.body)(ws, url)

  def fetchJson(ws: WSClient, url: String): Future[JsValue] =
    fetch(_.json)(ws, url)

  def scrapeHtml[T](ws: WSClient, url: String)(parser: String => T)(implicit w: Writes[T]): Future[Result] =
    scrape(fetchHtml)(ws, url)(parser)

  def scrapeJson[T](ws: WSClient, url: String)(parser: JsValue => T)(implicit w: Writes[T]): Future[Result] =
    scrape(fetchJson)(ws, url)(parser)

  def write[T](result: T)(implicit w: Writes[T]): Result =
    Ok(Json.obj(
      "result" -> result
    )).withHeaders("Content-Type" -> "application/json; charset=utf-8")


  private def fetch[U](extract: WSResponse => U)(ws: WSClient, url: String): Future[U] =
    ws.url(url).withRequestTimeout(3000.millis).get().map(response => extract(response))

  def scrape[T, U](fetch: (WSClient, String) => Future[U])(ws: WSClient, url: String)(parser: U => T)(implicit w: Writes[T]): Future[Result] = {
    fetch(ws, url)
      .map { value => write(parser(value)) }
      .recover {
      case e: Exception =>
        NotFound(Json.obj(
          "message" -> s"Unable to connect to $url",
          "error" -> e.getMessage)
        ).withHeaders("Content-Type" -> "application/json; charset=utf-8")
    }
  }
}
