package backend.models

import backend.models.User.UserId
import common.models.Email
import play.api.libs.json._

case class User(
  id: UserId,
  firstName: String,
  lastName: String,
  email: Email)
object User {
  trait UserIdType
  type UserId = Id[UserIdType]
  val  UserId = Id.apply[UserIdType] _

  //implicit val format = Json.format[User] // UserId => No apply function found matching unapply parameters :(
  implicit def jsonReads: Reads[User] = new Reads[User] {
    def reads(json: JsValue): JsResult[User] = JsSuccess(User(
      (json \ "id").as[UserId],
      (json \ "firstName").as[String],
      (json \ "lastName").as[String],
      (json \ "email").as[Email]))
  }
  implicit def jsonWrites: Writes[User] = new Writes[User] {
    def writes(user: User): JsValue = Json.obj(
      "id" -> user.id,
      "firstName" -> user.firstName,
      "lastName" -> user.lastName,
      "email" -> user.email)
  }
}
