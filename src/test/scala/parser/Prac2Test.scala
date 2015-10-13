package parser

import _root_.java.util

import org.scalatest._

import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.combinator.Parsers

/**
 *
 * Created by puri on 10/6/2015.
 */
class Prac2Test extends FlatSpec with Matchers with
    OptionValues with Inside with Inspectors{


  "Annotation" should "work as expimport scala.util.parsing.combinator.RegexParsersected in" in {

    object Temp extends RegexParsers{
      val p = new JavaP
      def apply(s:String): Temp.ParseResult[JAnno] = parseAll(p.annotation.asInstanceOf[Parser[JAnno]],s)
    }

    println(Temp("""@deprecated(value = "asdf")"""))
    assert(Temp("""@deprecated""").successful)
    assert(Temp("""@deprecated("asdf")""").successful)
    assert(Temp("""@deprecated(value = "asdf")""").successful)
    println("asdf")

  }

  "Instance Variable" should "work as expected" in {
    object Temp2 extends RegexParsers{
      val p = new JavaP
      def apply(s:String) = parseAll(p.instanceVariables.asInstanceOf[Parser[JInstanceVariable]],s)
    }
    println(Temp2("""public static int i = 23;"""))
    assert(Temp2(""";""").successful)
    assert(Temp2("""static int i = 23;""").successful)
    assert(Temp2("""public static int i = 23;""").successful)
    assert(Temp2("""public static final float i = 23.0;""").successful)
    assert(!Temp2("""public static String a = "23""").successful)
    assert(Temp2("""List<String> ls = new ArrayList<>;""").successful)

  }

  "Static blocks" should "well work" in {
    object Temp2 extends RegexParsers{
      val p = new JavaP
      def apply(s:String) = parseAll(p.staticBlock.asInstanceOf[Parser[String]],s)
    }
    assert(Temp2(
      """static {
        |}
      """.stripMargin).successful)
    assert(Temp2(
      """static {
        |int i = 23;
        |int j = get();
        |}
      """.stripMargin).successful)
    assert(Temp2(
      """static {
        |int j = 23;{
        | int j = 23;
        |{
        |}}}
      """.stripMargin).successful)
    val margin: String =
      """static {
        |final int j = 23;
        |int i = get();
        |}
      """.stripMargin
    assert(Temp2(margin).successful)
    println(Temp2(margin))
  }

  "Methods" should "work as expected" in {
    object Temp extends RegexParsers {
      val p = new JavaP
      def apply(s: String) = parseAll(p.method.asInstanceOf[Parser[JMethod]], s)
    }

    assert(Temp("""public void go(){}""").successful)
    assert(Temp(
      """public void go(int i, Object s){
        | sout(i);
        |}
      """.stripMargin).successful)
    assert(Temp(
      """
        |public <T> List<T> ls(List<T> ls){
        | return ls;
        |}
      """.stripMargin).successful)
    assert(Temp(
      """public static void shuffle(List<?> list) {
        |        Random rnd = r;
        |        if (rnd == null)
        |            r = rnd = new Random(); // harmless race.
        |        shuffle(list, rnd);
        |    }
      """.stripMargin).successful)
    println(Temp(
      """
        |public static <T> int binarySearch(List<? extends T> list, T key, Comparator<? super T> c) {
        |        if (c==null){
        |            return binarySearch((List<? extends Comparable<? super T>>) list, key);}
        |
        |        else{
        |            return Collections.iteratorBinarySearch(list, key, c);
        |            }
        |    }
      """.stripMargin))
  }

}
