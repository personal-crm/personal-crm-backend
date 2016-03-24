package backend.models

import common.models.utils.TStringHelper
import common.models.{Tag, Text, TextMultiline}
import org.joda.time.DateTime
import play.api.libs.json._

trait EntityIdType
case class EntityId(override val value: String) extends Id[EntityIdType](value)
object EntityId extends TStringHelper[EntityId] {
  def build(value: String) = EntityId(value)
}
case class Entity(
  id: EntityId,
  category: EntityCategory,
  name: Text,
  description: TextMultiline,
  tags: Seq[Tag],
  archived: Boolean,
  created: DateTime,
  updated: DateTime)
object Entity {
  import EntityId.format
  implicit val formatEntity = Json.format[Entity]
}

sealed trait EntityCategory
case object EntityCategoryPerson extends EntityCategory
case object EntityCategoryCompany extends EntityCategory
case object EntityCategoryPlace extends EntityCategory
object EntityCategory {
  implicit def format: Format[EntityCategory] = new Format[EntityCategory] {
    override def reads(json: JsValue): JsResult[EntityCategory] = json.asOpt[String] match {
      case Some("EntityCategoryPerson") => JsSuccess(EntityCategoryPerson)
      case Some("EntityCategoryCompany") => JsSuccess(EntityCategoryCompany)
      case Some("EntityCategoryPlace") => JsSuccess(EntityCategoryPlace)
      case _ => JsError(s"Unknown EntityCategory: $json")
    }
    override def writes(o: EntityCategory): JsValue = JsString(o.toString)
  }
}
