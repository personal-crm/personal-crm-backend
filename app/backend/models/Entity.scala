package backend.models

import backend.models.Entity.EntityId
import common.models.{Text, TextMultiline, Tag}
import org.joda.time.DateTime
import play.api.libs.json._

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
  trait EntityIdType
  type EntityId = Id[EntityIdType]
  val  EntityId = Id.apply[EntityIdType] _

  //implicit val format = Json.format[Entity] // EntityId => No apply function found matching unapply parameters :(
  implicit def jsonReads: Reads[Entity] = new Reads[Entity] {
    def reads(json: JsValue): JsResult[Entity] = JsSuccess(Entity(
      (json \ "id").as[EntityId],
      (json \ "category").as[EntityCategory],
      (json \ "name").as[Text],
      (json \ "description").as[TextMultiline],
      (json \ "tags").as[Seq[Tag]],
      (json \ "archived").as[Boolean],
      (json \ "created").as[DateTime],
      (json \ "updated").as[DateTime]))
  }
  implicit def jsonWrites: Writes[Entity] = new Writes[Entity] {
    def writes(entity: Entity): JsValue = Json.obj(
      "id" -> entity.id,
      "category" -> entity.category,
      "name" -> entity.name,
      "description" -> entity.description,
      "tags" -> entity.tags,
      "archived" -> entity.archived,
      "created" -> entity.created,
      "updated" -> entity.updated)
  }
}

sealed trait EntityCategory
case object EntityCategoryPerson extends EntityCategory
case object EntityCategoryCompany extends EntityCategory
case object EntityCategoryPlace extends EntityCategory
object EntityCategory {
  implicit def jsonReads: Reads[EntityCategory] = new Reads[EntityCategory] {
    def reads(json: JsValue): JsResult[EntityCategory] = json.asOpt[String] match {
      case Some("EntityCategoryPerson") => JsSuccess(EntityCategoryPerson)
      case Some("EntityCategoryCompany") => JsSuccess(EntityCategoryCompany)
      case Some("EntityCategoryPlace") => JsSuccess(EntityCategoryPlace)
      case _ => JsError(s"Unknown EntityCategory: $json")
    }
  }
  implicit def jsonWrites: Writes[EntityCategory] = new Writes[EntityCategory] {
    def writes(entityCategory: EntityCategory): JsValue = JsString(entityCategory.toString)
  }
}
