package shapeprac


import shapeless.Poly1
import shapeless.PolyDefns.~>

object Prac1 {
  def main(args: Array[String]) {

  }

  object choose extends (Set ~> Option) {
    def apply[T](s : Set[T]) = s.headOption
  }

  val x: Option[Int] = choose(Set(1,2,3))
  val y: Option[String] = choose(Set("a","b"))

  val z: Option[Set[_ >: Int with Symbol]] = choose(Set(Set(1), Set('a)))

  def pairApply(f: Set ~> Option) = (f(Set(1, 2, 3)), f(Set('a', 'b', 'c')))
  pairApply(choose)

  List(Set(1, 3, 5), Set(2, 4, 6)) map choose

  object size extends Poly1 {
    implicit def caseInt = at[Int](x => 1)
    implicit def caseString = at[String](_.length)
    implicit def caseTuple[T, U]
    (implicit st : Case.Aux[T, Int], su : Case.Aux[U, Int]) =
      at[(T, U)](t => size(t._1)+size(t._2))
  }
  size((23, "foo"))

}

