package backend.models

import common.models.Email
import common.models.utils.TStringHelper
import play.api.libs.json._

trait UserIdType
case class UserId(override val value: String) extends Id[UserIdType](value)
object UserId extends TStringHelper[UserId] {
  def build(value: String) = UserId(value)
}
case class User(
  id: UserId,
  firstName: String,
  lastName: String,
  email: Email)
object User {
  import UserId.format
  implicit val formatUser = Json.format[User]
}
