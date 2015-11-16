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

  def block: Parser[JBlock] = "{" ~> rep(opt("[^{^}]*".r) ~ opt(block)) <~ "}" ^^ {x => JBlock(x.toString)}




  val blockC = new Parser[JBlock] {
    override def apply(in: Input): ParseResult[JBlock] = {
      var read = in
      val ans = new StringBuilder
      var count = 0
      while(!read.atEnd){
        val x = read.first
        if(x == '{'){
          count = count+1
        }else if(x == '}'){
          count = count -1
        }
        ans.+(x)
        read = read.drop(1)
      }
      if(count==0)
        Success(JBlock(ans.toString), read)
      else Failure("Error. Block not completed well", read)
    }
  }


  def staticBlock: Parser[String] = "static" ~> block ^^ {
    case x => x.toString
  }

  def modifiers:Parser[List[String]] = rep("""(public|protected|private|static)""".r) ^^ {case x => x}

  def annotation: Parser[JAnno] = "@"~>name <~ opt("(" ~> """[^)]*""".r <~ ")") ^^ { x => JAnno(x)}
  /*
  MethodDeclaration:
    MethodHeader MethodBody

  MethodHeader:
    MethodModifiersopt TypeParametersopt Result MethodDeclarator Throwsopt

  MethodDeclarator:
    Identifier ( FormalParameterListopt )
   */
  def met =
    modifiers ~ opt("<" ~> """[\w,]*""".r <~ ">") ~ objectType ~ methodDeclarator ~ opt("throws" ~> "[^{]*".r) ~ block ^^ {
      case modifier ~ generic ~ result ~ md ~ thr ~ body => (md._1,body)
    }

  def methodDeclarator = (name <~ "(") ~ opt(repsep(opt(annotation) ~ (objectType ~ name),",")) <~ ")" ^^ {case n ~ args => (n, args.toString)}
  def objectType: Parser[String] = """[\w]*""".r ~ opt("<" ~> opt(rep("""(\?|extends|super)""".r)) ~> objectType <~ ">") ^^ {x => x.toString()}

  def method: Parser[JMethod] = opt(javadoc) ~ opt(annotation) ~ met ^^ {case j ~ a ~ n => JMethod(j, a, n._1,n._2.body)}

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
case class JMethod(doc:Option[JDoc], anno: Option[JAnno],name:String, body:String)
case class JMethodArg(name: List[String])
case class JClass(name:String, ls:List[JMethod])
case class JAnno(name:String)
case class JInstanceVariable(modifiers: List[String], line:String)
case class JBlock(body:String)