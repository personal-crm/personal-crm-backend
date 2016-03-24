package common.models

import common.models.utils.{TIntHelper, TInt}
import play.api.libs.json.{JsNumber, JsValue}

case class OneBasedInt(value: Int) extends TInt {
  require(value > 0, s"OneBasedInt value should be > 0 (actual: $value)")
  def next: OneBasedInt = OneBasedInt(value + 1)
}
object OneBasedInt extends TIntHelper[OneBasedInt] {
  def build(value: Int) = OneBasedInt(value)
  //implicit def toInt(i: OneBasedInt): Int = i.value
  //implicit def fromInt(i: Int): OneBasedInt = OneBasedInt(i)
}
