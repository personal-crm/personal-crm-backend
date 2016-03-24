package backend.controllers

import javax.inject._

import backend.models._
import backend.models.EntityId
import backend.models.StoryId
import backend.models.UserId
import common.models._
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() extends Controller {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    Ok(backend.views.html.index("Your new application is ready."))
  }

  def test = Action {
    Ok(Json.obj(
      "test" -> "OK",
      "page" -> Page(Seq(8), OneBasedInt(1), 10, 1),
      "user" -> User(UserId("123"), "Loïc", "Knuchel", Email("loicknuchel@gmail.com")),
      "entity" -> Entity(EntityId("456"), EntityCategoryPerson, Text("Loic Knuchel"), TextMultiline("Dévelopeur scala :)"), Seq(Tag("tag1"), Tag("tag2")), false, new DateTime(), new DateTime()),
      "story" -> Story(StoryId("789"), Text("Première note :)"), TextMultiline("Top !\nMerci @Loïc Knuchel"), new DateTime(), Seq(Tag("tag1"), Tag("tag2")), Seq(StoryEntity(EntityId("456"), EntityCategoryPerson, Text("Loïc Knuchel"))), false, new DateTime(), new DateTime())
    )).withHeaders("Content-Type" -> "application/json; charset=utf-8")
  }

}
