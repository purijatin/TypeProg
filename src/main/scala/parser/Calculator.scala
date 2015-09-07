package parser

import scala.util.parsing.combinator.RegexParsers

object Calculator extends Cal {
  def main(args: Array[String]) {
    assert(!parse(exp, "a").successful)
    assert(parseAll(exp, "1").get == 1)
    assert(parseAll(exp, "1+1").get == 2)
    assert(parseAll(exp, "1*1").get == 1)
    assert(parseAll(exp, "1/1").get == 1)
    assert(parseAll(exp, "1-1").get == 0)
    println(parseAll(exp, "1+1-1/1*1"))
    assert(parseAll(exp, "1+1-1/1*1").get == 1)
    println("------------------")
    assert(!parseAll(exp, "1-").successful)
    assert(parseAll(exp, "1-1").get == 0)
    assert(parseAll(exp, "10+6-2/7*2").get == 4)
    assert(parseAll(exp, "10 + 6- 2/ 7*2").get == 4)
    assert(!parseAll(exp, "10 + 6- 2/ 7*").successful)
    assert(!parseAll(exp, "(1+1").successful)
    assert(parseAll(exp, "(1+1)").get == 2)
    assert(parseAll(exp, "6+(10/5)").get == 8)
  }
}

class Cal extends RegexParsers {
  def number: Parser[Double] = """\d+(\.\d*)?""".r ^^ { _.toDouble}

  def factor = number | ("(" ~> expression <~ ")")

  def expression: Parser[Double] = factor ~
    rep("*" ~ log(factor)("multiply")
    | "/" ~ log(factor)("divide")
    | "+" ~ log(factor)("plus")
    | "-" ~ log(factor)("minus")) ^^ {
    case num ~ ls => ls.foldLeft(num)((value, elem) => elem match {
      case "+" ~ y => value + y
      case "-" ~ y => value - y
      case "*" ~ y => value * y
      case "/" ~ y => value / y
    })
  }

  def exp = expression

}