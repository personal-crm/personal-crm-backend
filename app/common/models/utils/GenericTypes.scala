package common.models.utils

import play.api.libs.json._

// cf https://github.com/saloonapp/backend/blob/master/app/common/models/utils/tString.scala

trait TPrimitive[T] {
  val value: T
  override def toString: String = value.toString
}
trait TPrimitiveHelper[U, T <: TPrimitive[U]] {
  val className: String
  def build(value: U): T
  def fromJson(json: JsValue): Option[U]
  def toJson(t: T): JsValue

  implicit def jsonReads: Reads[T] = new Reads[T] {
    def reads(json: JsValue): JsResult[T] = fromJson(json).map(v => JsSuccess(build(v))).getOrElse(JsError(s"$className expected (actual: $json)"))
  }
  implicit def jsonWrites: Writes[T] = new Writes[T] {
    def writes(t: T): JsValue = toJson(t)
  }
}

trait TString extends TPrimitive[String]
trait TStringHelper[T <: TString] extends TPrimitiveHelper[String, T] {
  val className = "String"
  def fromJson(json: JsValue): Option[String] = json.asOpt[String]
  def toJson(t: T): JsValue = JsString(t.value)
}

trait TInt extends TPrimitive[Int]
trait TIntHelper[T <: TInt] extends TPrimitiveHelper[Int, T] {
  val className = "Int"
  def fromJson(json: JsValue): Option[Int] = json.asOpt[Int]
  def toJson(t: T): JsValue = JsNumber(t.value)
}
