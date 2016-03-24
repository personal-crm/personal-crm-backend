package backend.models

import backend.models.Entity.EntityId
import backend.models.Story.StoryId
import common.models.{TextMultiline, Text, Tag}
import org.joda.time.DateTime
import play.api.libs.json._

case class StoryEntity(
  id: EntityId,
  category: EntityCategory,
  label: Text)
object StoryEntity {
  //implicit val format = Json.format[StoryEntity] // EntityId => No apply function found matching unapply parameters :(
  implicit def jsonReads: Reads[StoryEntity] = new Reads[StoryEntity] {
    def reads(json: JsValue): JsResult[StoryEntity] = JsSuccess(StoryEntity(
      (json \ "id").as[EntityId],
      (json \ "category").as[EntityCategory],
      (json \ "label").as[Text]))
  }
  implicit def jsonWrites: Writes[StoryEntity] = new Writes[StoryEntity] {
    def writes(storyEntity: StoryEntity): JsValue = Json.obj(
      "id" -> storyEntity.id,
      "category" -> storyEntity.category,
      "label" -> storyEntity.label)
  }
}
case class Story(
  id: StoryId,
  title: Text,
  text: TextMultiline,
  date: DateTime,
  tags: Seq[Tag],
  entities: Seq[StoryEntity],
  archived: Boolean,
  created: DateTime,
  updated: DateTime)
object Story {
  trait StoryIdType
  type StoryId = Id[StoryIdType]
  val  StoryId = Id.apply[StoryIdType] _

  //implicit val format = Json.format[Story] // EntityId => No apply function found matching unapply parameters :(
  implicit def jsonReads: Reads[Story] = new Reads[Story] {
    def reads(json: JsValue): JsResult[Story] = JsSuccess(Story(
      (json \ "id").as[StoryId],
      (json \ "title").as[Text],
      (json \ "text").as[TextMultiline],
      (json \ "date").as[DateTime],
      (json \ "tags").as[Seq[Tag]],
      (json \ "entities").as[Seq[StoryEntity]],
      (json \ "archived").as[Boolean],
      (json \ "created").as[DateTime],
      (json \ "updated").as[DateTime]))
  }
  implicit def jsonWrites: Writes[Story] = new Writes[Story] {
    def writes(story: Story): JsValue = Json.obj(
      "id" -> story.id,
      "title" -> story.title,
      "text" -> story.text,
      "date" -> story.date,
      "tags" -> story.tags,
      "entities" -> story.entities,
      "archived" -> story.archived,
      "created" -> story.created,
      "updated" -> story.updated)
  }
}
