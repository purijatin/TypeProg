package typeprog

/**
 *
 * Created by jatin on 11/7/15.
 */
object Sort{
  def main(args: Array[String]) {

  }

  sealed trait Bool{
    type If[T <: Up,F <: Up,Up] <: Up
    type &&[A <: Bool, B <: Bool] = A#If[B,False,Bool]
    type ||[A <: Bool, B<: Bool] = A#If[True, B, Bool]
    type Not[A<:Bool] = A#If[False, True, Bool]
  }

  sealed trait True extends Bool {
    type If[T <: Up, F <: Up, Up] = T
  }
  sealed trait False extends Bool {
    type If[T <: Up, F <: Up, Up] = F
  }


  sealed trait IntType{
    type Plus[Other <:IntType] <: IntType
    type Match[NonZero[_ <: IntType] <: Up, IfZero <: Up, Up] <: Up

    type <= [Other <: IntType] <: Bool
    type >[Other <: IntType] <: Bool
  }

  sealed trait Int0 extends IntType{
    type Plus[other <:IntType] = other
    type Match[NonZero[_ <: IntType] <: Up, IfZero <: Up, Up]  = IfZero
    type <= [other <:IntType] = True
    type > [Other <: IntType] = False
  }
  sealed trait IntN[Prev <: IntType] extends IntType{
    type Plus[other <:IntType] = IntN[Prev#Plus[other]]
    type Match[NonZero[_ <: IntType] <: Up, IfZero <: Up, Up] = NonZero[Prev]

    type <= [other <: IntType] = other#Match[Prev# <=,False, Bool]
    type > [Other <: IntType] = Other#Match[Prev# >,True, Bool]
  }


  type _1 = IntN[Int0]
  type _2 = IntN[_1]

  implicitly[_2# <=[_1] =:= False]
  implicitly[_1# <=[_2] =:= True]
  implicitly[_1# >[_2] =:= False]
  implicitly[_2# >[_1] =:= True]
  implicitly[_2#Match[IntN,Int0,IntType] =:= IntN[IntN[Int0]]]

}
