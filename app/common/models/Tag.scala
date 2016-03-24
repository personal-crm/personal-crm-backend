package common.models

import common.models.utils.{TStringHelper, TString}

case class Tag(value: String) extends TString {
  require(Tag.isTag(value), s"Tag value should not contains space char (actual: $value)")
}
object Tag extends TStringHelper[Tag] {
  def build(value: String) = Tag(value)
  def isTag(value: String): Boolean = value.replaceAll(" ", "").length == value.length
}
