package sprayjson

import spray.json._
import DefaultJsonProtocol._

object Prac {
  def main(args: Array[String]) {
    val source = """{ "some": "JSON source" }"""
    case class MyObjectType(some:String)
    object MyJsonProtocol extends DefaultJsonProtocol {
      implicit val colorFormat = jsonFormat1(MyObjectType)
    }
    import MyJsonProtocol._

    val jsonAst: JsValue = source.parseJson
    val a2 = jsonAst.convertTo[MyObjectType]
    println(jsonAst)
    println(a2)
  }
}
