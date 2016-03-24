package backend.models

import common.models.utils.TString
import play.api.libs.json._

class Id[T](override val value: String) extends TString
object Id {
  implicit def jsonReads[T]: Reads[Id[T]] = new Reads[Id[T]] {
    def reads(json: JsValue): JsResult[Id[T]] = json.asOpt[String].map(v => JsSuccess(new Id[T](v))).getOrElse(JsError(s"String expected (actual: $json)"))
  }
  implicit def jsonWrites[T]: Writes[Id[T]] = new Writes[Id[T]] {
    def writes(id: Id[T]): JsValue = JsString(id.value)
  }
}
