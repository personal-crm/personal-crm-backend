package common

object StringUtils {

  implicit class StringImprovements(val s: String) {
    import scala.util.control.Exception._
    def toOpt = if(s.length == 0) None else Some(s)
    def toIntOpt = catching(classOf[NumberFormatException]) opt s.toInt
    def toIntOrElse(default: Int) = s.toIntOpt.getOrElse(default)
  }

}
