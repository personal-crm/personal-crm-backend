package tools.scrapers

import play.api.libs.json.{Json, Writes}
import play.api.libs.ws.WSClient
import play.api.mvc.Result
import play.api.mvc.Results.{NotFound, Ok}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

object ScraperUtils {
  def fetch[T](ws: WSClient, url: String)(parser: String => T)(implicit w: Writes[T]): Future[Result] = {
    ws.url(url).withRequestTimeout(3000.millis).get().map { response =>
      Ok(Json.obj(
        "result" -> parser(response.body)
      )).withHeaders("Content-Type" -> "application/json; charset=utf-8")
    }.recover {
      case e: Exception =>
        NotFound(Json.obj(
          "message" -> s"Unable to connect to $url",
          "error" -> e.getMessage)
        ).withHeaders("Content-Type" -> "application/json; charset=utf-8")
    }
  }
}
