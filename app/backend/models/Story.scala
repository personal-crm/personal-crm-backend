package backend.models

import common.models.{Tag, Text, TextMultiline}
import org.joda.time.DateTime
import play.api.libs.json._

case class StoryEntity(
  id: EntityId,
  category: EntityCategory,
  label: Text)
object StoryEntity {
  import Entity.entityIdFormat
  implicit val  storyEntityFormat = Json.format[StoryEntity]
}


trait StoryIdType

case class StoryId(override val value : String) extends Id[StoryIdType](value)
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

  implicit val storyIdFormat = Json.format[StoryId]
  implicit val storyFormat = Json.format[Story]
}
