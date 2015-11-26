package prac

import scala.reflect.ClassTag
import scala.util.Try


object TypeClasss {

  case class Person(name: String, age: Double)
  case class Book(title: String, author: String, year: Int)
  case class Country(name: String, population: Int, area: Double)
  case class Empty()
  case class Wrap(p:Person)

  object ReflectiveRowParser {
    def apply[T: ClassTag](s: String): Option[T] = Try {
      val ctor = implicitly[ClassTag[T]].runtimeClass.getConstructors.head
      val paramsArray = s.split(",").map(_.trim)
      val paramsWithTypes: Array[(String, Class[_])] = paramsArray.zip(ctor.getParameterTypes)

      val parameters = paramsWithTypes.map {
        case (param: String, cls) => cls.getName match {
          case "int" => param.toInt.asInstanceOf[Object]
          case "double" => param.toDouble.asInstanceOf[Object]
          case _ =>
            val paramConstructor = cls.getConstructor(param.getClass)
            paramConstructor.newInstance(param).asInstanceOf[Object]
        }
      }

      ctor.newInstance(parameters: _*).asInstanceOf[T]
    }.toOption
  }

  import shapeless._

  trait Parser[A] {
    def apply(s: String): Option[A]
  }

  object Parser {
    def apply[A](s: String)(implicit parser: Parser[A]): Option[A] = parser(s)

    implicit val stringParser: Parser[String] = new Parser[String] {
      def apply(s: String): Option[String] = Some(s)
    }

    implicit val intParser: Parser[Int] = new Parser[Int] {
      def apply(s: String): Option[Int] = Try(s.toInt).toOption
    }

    implicit val doubleParser: Parser[Double] = new Parser[Double] {
      def apply(s: String): Option[Double] = Try(s.toDouble).toOption
    }

    implicit val hnilParser: Parser[HNil] = new Parser[HNil] {
      def apply(s: String): Option[HNil] = if (s.isEmpty) Some(HNil) else None
    }

    implicit def hconsParser[H: Parser, T <: HList: Parser]: Parser[H :: T] = new Parser[H :: T] {
      def apply(s: String): Option[H :: T] = s.split(",").toList match {
        case cell +: rest => for {
          head <- implicitly[Parser[H]].apply(cell)
          tail <- implicitly[Parser[T]].apply(rest.mkString(","))
        } yield head :: tail
      }
    }


    implicit def caseClassParser[A, R <: HList](implicit
                                                gen: Generic[A] { type Repr = R },
                                                reprParser: Parser[R]
                                                 ): Parser[A] = new Parser[A] {
      def apply(s: String): Option[A] = reprParser.apply(s).map(x => gen.from(x))
    }


  }

  def main(args: Array[String]) {
//    ReflectiveRowParser[List[Book]]("Hamlet,Shakespeare,1600")
    Parser[Person]("Amy,54.2")
    Parser[Book]("Hamlet,Shakespeare")
    Parser[Empty]("Empty")
//    Parser[Wrap]("Wrap")
  }
}
