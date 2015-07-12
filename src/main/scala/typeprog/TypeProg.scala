package typeprog

/**
 *
 * Created by jatin on 9/7/15.
 */
object TypeProg extends App{

  sealed trait IntType{
    type Plus[That <: IntType] <: IntType
  }

  sealed trait IntType0 extends IntType{
    override type Plus[That <: IntType] = That
  }

  sealed trait IntTypeN[Prev <: IntType] extends IntType{
    override type Plus[That <: IntType] = IntTypeN[Prev#Plus[That]]
  }

  type intT1 = IntTypeN[IntType0]
  type intT2 = IntTypeN[intT1]

  implicitly[intT1 =:= IntType0#Plus[intT1]]
  implicitly[intT2 =:= intT1#Plus[intT1]]

  sealed trait IntList[Size <: IntType]{
    def ::(head:Int): IntList[IntTypeN[Size]] = IntListImpl(head, this)
    def + (that:IntList[Size]): IntList[Size]
    def ++[ThatSize <: IntType] (that: IntList[ThatSize]) : IntList[Size#Plus[ThatSize]]
  }
  case object IntNil extends IntList[IntType0]{
    override def + (that:IntList[IntType0]):IntList[IntType0] = this
    override def ++[ThatSize <: IntType] (that: IntList[ThatSize]) : IntList[ThatSize] = that
  }
  case class IntListImpl[TailSize <: IntType](head:Int, tail: IntList[TailSize]) extends IntList[IntTypeN[TailSize]]{
    override def + (that:IntList[IntTypeN[TailSize]]): IntList[IntTypeN[TailSize]] = that match {
      case IntListImpl(h, t) => (head + h) :: (tail + t)
    }

    override def ++[ThatSize <: IntType] (that: IntList[ThatSize]) : IntList[IntTypeN[TailSize]#Plus[ThatSize]] = IntListImpl(head, tail ++ that)
  }

  val a = 1 :: 2 :: IntNil
  val b = 1 :: 4 :: IntNil
  val c = a + b
  println(c == (2 :: 6 :: IntNil))
  println(a ++ b)

}
