package common.models

import common.models.utils.{TIntHelper, TInt}

case class OneBasedInt(value: Int) extends TInt {
  require(value > 0, s"OneBasedInt value should be > 0 (actual: $value)")
  def next: OneBasedInt = OneBasedInt(value + 1)
}
object OneBasedInt extends TIntHelper[OneBasedInt] {
  val first = OneBasedInt(1)
  def build(value: Int) = OneBasedInt(value)
}
