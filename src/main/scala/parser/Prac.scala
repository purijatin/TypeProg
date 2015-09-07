package parser

import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.input.{CharArrayReader, Positional}

object Prac extends LoopParser{
  def main(args: Array[String]) {
    go("for x in 1 to 42 { for y in 0 to 1 {} }")
    go(
      """for x in 1 to 42
        | for y in 0 to 1 {
        |
        | } """
        .stripMargin)


    parseAll(loop, new CharArrayReader("for x in 1 to 42 { for y in 0 to 1 {} }".toArray)) match {
      case Success(lup, _) => {
        println(lup)
        val p = lup.pos
        println(p)
      }
      case x => println(x)
    }


  }

  def go(s:String): Unit ={
    parseAll(loop, s) match {
      case Success(lup,_) => println(lup)
      case x => println(x)
    }
  }
}

class LoopParser extends RegexParsers {
  override type Elem = Char
  def identifier  = """[_\p{L}][_\p{L}\p{Nd}]*""".r
  def integer: Parser[Int] = """(0|[1-9]\d*)""".r ^^ { _.toInt }

  def loop =
    positioned(("for" ~> identifier) ~ ("in" ~> integer) ~ ("to" ~> integer) ~ statement ^^
      { case variable ~ lBound ~ uBound ~ statement => ForLoop(variable, lBound, uBound,statement) })

  def statement : Parser[Statement] = loop | block
  def statements: Parser[List[Statement]] = statement*
  def block = positioned("{"~>statements<~"}"  ^^ { l => Block(l) })



}

abstract trait Statement extends Positional
case class Block(statements : List[Statement]) extends Statement
case class ForLoop(variable: String, lowerBound:Int, upperBound: Int, statement:Statement) extends Statement


