package common.models

import common.models.utils.{TStringHelper, TString}

case class TextMultiline(value: String) extends TString
object TextMultiline extends TStringHelper[TextMultiline] {
  def build(value: String) = TextMultiline(value)
}
