package common.models

import common.models.utils.{TStringHelper, TString}

case class Email(value: String) extends TString {
  require(Email.isEmail(value), s"Incorrect Email value (actual: $value)")
}
object Email extends TStringHelper[Email] {
  def build(value: String) = Email(value)
  def isEmail(value: String): Boolean = value.contains("@") // TODO
}
