package parser


import scala.util.parsing.combinator.RegexParsers


object Prac2 extends App{
  println(new JavaP().apply(
    """class Prac<T>{
      |private final int i =23;
      |
      |@deprecated("asdf")
      |public void go(){
      | sout("asdf");
      |}
      |}""".stripMargin))
}


class JavaP extends RegexParsers{

  def javadoc = """/\*\*([^\*]|\*(?!/))*.*?\*/""".r ^^ { x=>
    JDoc(x)
  }

  def instanceVariables: Parser[JInstanceVariable] = opt( """(public|protected|private)""".r | "static" | "final") ~ ("""[^;]*""".r <~ ";") ^^ {
    case modifiers ~ line => JInstanceVariable(modifiers.fold(List[String]())(x => List(x)), line)
  }

  def block: Parser[JBlock] = "{" ~> "[^}]*" <~ "}" ^^ {x => JBlock(x)}
  def nestedBlocks = "{" ~> rep((("[^{^}]*".r) | (block))) <~ "}"

  val blockC = new Parser[String] {
    override def apply(in: Input): ParseResult[String] = {
      var read = in
      val ans = new StringBuilder
      var count = 0
      while(count > 0){
        if(in.atEnd)
          return Failure("Stream ended but block not.", read)

        val x = read.first
        if(x == '{'){
          count = count+1
        }else if(x == '}'){
          count = count -1
        }
        ans.+(x)
        read = read.drop(1)
      }
      Success(ans.toString, read)
    }
  }


  def staticBlock: Parser[String] = "static" ~> blockC ^^ {
    case x => x.toString
  }

  def annotation: Parser[JAnno] = "@"~>name <~ opt("(" ~> """[^)]*""".r <~ ")") ^^ { x => JAnno(x)}
  def method = opt(javadoc) ~ """(public|protected|private|static|\s) +[\w\<\>\[\]]+\s+(\w+) *\([^\)]*\) *(\{?|[^;])""".r ^^ { x =>

  }

  def clas: Parser[JClass] = "class " ~ className ~ ("{" ~ classBody <~ "}")^^ {
    case c ~ n ~ b => JClass(n, List())
  }

  def classBody = anyChar
  def anyChar: Parser[String] = "[]".r ^^ { x => x}
  def className: Parser[String] = "[\\w]*(<.*>)?".r ^^ { x => x}
  def name = "[\\w]*".r ^^ { x => x}

  def file =  clas ^^ {x =>
    println(x)
    x
  }

  def apply(st: String) = parseAll(file, st)
}

case class JDoc(body:String)
case class JMethod(doc:JDoc, name:String, body:String)
case class JClass(name:String, ls:List[JMethod])
case class JAnno(name:String)
case class JInstanceVariable(modifiers: List[String], line:String)
case class JBlock(body:String)