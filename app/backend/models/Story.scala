package backend.models

import common.models.utils.TStringHelper
import common.models.{Tag, Text, TextMultiline}
import org.joda.time.DateTime
import play.api.libs.json._

trait StoryIdType
case class StoryId(override val value : String) extends Id[StoryIdType](value)
object StoryId extends TStringHelper[StoryId] {
  def build(value: String) = StoryId(value)
}
case class StoryEntity(
  id: EntityId,
  category: EntityCategory,
  label: Text)
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
  import StoryId.format
  import EntityId.{format => entityIdFormat}
  implicit val formatStoryEntity = Json.format[StoryEntity]
  implicit val formatStory = Json.format[Story]
}
