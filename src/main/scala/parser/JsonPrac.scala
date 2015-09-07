package parser

import scala.util.matching.Regex
import scala.util.parsing.combinator.RegexParsers


object JsonPrac {
  def main(args: Array[String]) {
    assert(JParser( """[1,2,3]""").successful)
    assert(JParser( """
                   {"id": "ZoomIn"}
                      """.stripMargin).successful)
    assert(JParser(
      """
        |{"menu": {
        |    "header": "SVG Viewer",
        |    "items": [
        |        {"id": "Open"},
        |        {"id": "OpenNew", "label": "Open New"},
        |        null,
        |        {"id": "ZoomIn", "label": "Zoom In"},
        |        {"id": "ZoomOut", "label": "Zoom Out"},
        |        {"id": "OriginalView", "label": "Original View"},
        |        null,
        |        {"id": "Quality"},
        |        {"id": "Pause"},
        |        {"id": "Mute"},
        |        null,
        |        {"id": "Find", "label": "Find..."},
        |        {"id": "FindAgain", "label": "Find Again"},
        |        {"id": "Copy"},
        |        {"id": "CopyAgain", "label": "Copy Again"},
        |        {"id": "CopySVG", "label": "Copy SVG"},
        |        {"id": "ViewSVG", "label": "View SVG"},
        |        {"id": "ViewSource", "label": "View Source"},
        |        {"id": "SaveAs", "label": "Save As"},
        |        null,
        |        {"id": "Help"},
        |        {"id": "About", "label": "About Adobe CVG Viewer..."}
        |    ]
        |}}
        |
      """.stripMargin).successful)
    assert(JParser( """[1,2,3]""").successful)


  }

}

object JParser extends RegexParsers {
  def string = ("\"" ~> "[^\"]*".r <~ "\"") ^^ {
    case y => JElem(y)
  }

  def number: Parser[Double] = """\d+(\.\d*)?""".r ^^ {_.toDouble}

  def boolean: Parser[String] = "true" | "false" | "null"

  def value: JParser.Parser[JElem] = string | (number | boolean) ^^ { x => JElem(x) }

  def rowElement = (string <~ ":") ~ expr

  def jObject: JParser.Parser[JObject] = "{" ~> (rep(rowElement <~ ",") ~ rowElement) <~ "}" ^^ {
    case x ~ a  =>
      val map = x.map {
        case key ~ value => (key.ob.toString, value)
      }
      JObject(((a._1.ob.toString, a._2) :: map).toMap)//TODO remove toString
  }

  def jArray = "[" ~> (rep((value | jObject) <~ ",") ~ (value | jObject)) <~ "]" ^^ {
    case arr ~ num => JArray((num :: arr).toArray)
  }

  def expr: Parser[JNode] = log(jArray | jObject | value)("Parsing")

  def apply(st: String) = parseAll(expr, st)
}

sealed trait JNode

case class JObject(map: Map[String, JNode]) extends JNode

case class JArray(array: Array[JNode]) extends JNode

case class JElem(ob: Any) extends JNode

