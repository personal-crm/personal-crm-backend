package common.models

import common.models.utils.{TStringHelper, TString}

case class Text(value: String) extends TString
object Text extends TStringHelper[Text] {
  def build(value: String) = Text(value)
}
