package common.models

import play.api.libs.json._

case class Page[+A](
  items: Seq[A],
  current: OneBasedInt,
  size: Int,
  total: Int) {
  // startElement is 0 based
  val startElement: Int = PageRequest.startElement(current, size)
  val pageCount: Int = Page.pageCount(size, total)
  def map[B](f: A => B) = this.copy(items = items.map(f))
}
object Page {
  def apply[A](elems: Seq[A], pageReq: PageRequest, total: Int) = new Page(elems, pageReq.current, pageReq.size, total)
  def pageCount(size: Int, total: Int): Int = total / size

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

case class PageRequest(
  current: OneBasedInt,
  size: Int) {
  // startElement is 0 based
  val startElement: Int = PageRequest.startElement(current, size)
  def next: PageRequest = this.copy(current = current.next)
}
object PageRequest {
  def apply(startElement: Int, size: Int) = new PageRequest(current(startElement, size), size)
  def first(size: Int): PageRequest = PageRequest(OneBasedInt.first, size)
  def startElement(current: OneBasedInt, pageSize: Int): Int = (current.value - 1) * pageSize
  def current(startElement: Int, pageSize: Int): OneBasedInt = OneBasedInt((startElement / pageSize) + 1)
}
