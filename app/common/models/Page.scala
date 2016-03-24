package common.models

import play.api.libs.json._

case class Page[+A](
  items: Seq[A],
  current: OneBasedInt,
  size: Int,
  total: Int) {
  lazy val startItem: Int = Page.startItem(current, size)
  def map[B](f: A => B) = this.copy(items = items.map(f))
}
object Page {
  val first = OneBasedInt(1)
  // startItem is 0 based
  def startItem(current: OneBasedInt, pageSize: Int): Int = (current.value - 1) * pageSize
  def current(startItem: Int, pageSize: Int): OneBasedInt = OneBasedInt((startItem / pageSize) + 1)

  implicit def jsonReads[T](implicit fmt: Reads[T]): Reads[Page[T]] = new Reads[Page[T]] {
    def reads(json: JsValue): JsResult[Page[T]] = JsSuccess(Page[T](
      (json \ "items").as[Seq[T]],
      (json \ "current").as[OneBasedInt],
      (json \ "size").as[Int],
      (json \ "total").as[Int]))
  }
  implicit def jsonWrites[T](implicit fmt: Writes[T]): Writes[Page[T]] = new Writes[Page[T]] {
    def writes(page: Page[T]): JsValue = Json.obj(
      "items" -> page.items,
      "current" -> Json.toJson(page.current),
      "size" -> page.size,
      "total" -> page.total)
  }
}
