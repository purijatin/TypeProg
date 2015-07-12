package typeprog

/**
 *
 * Created by jatin on 10/7/15.
 */
object TypeProg2 extends App{
  sealed trait Bool{
    type If[T <: Up,F <: Up,Up] <: Up
    type &&[A <: Bool, B <: Bool] = A#If[B,False,Bool]
    type ||[A <: Bool, B<: Bool] = A#If[True, B, Bool]
    type Not[A<:Bool] = A#If[False, True, Bool]

  }
  def toBoolean[B <: Bool](implicit b: BooleanRep[B]) = b.value

  sealed trait True extends Bool {
    type If[T <: Up, F <: Up, Up] = T
  }
  sealed trait False extends Bool {
    type If[T <: Up, F <: Up, Up] = F
  }

  println(implicitly[True#If[Int, Long, AnyVal] =:= Int])

  class BooleanRep[B <:Bool](val value:Boolean)

  implicit val trueRep = new BooleanRep[True](true)
  implicit val falseRep = new BooleanRep[False](false)

  //---------------------------------------------------------------------------------

  trait Fold[-Elem, Value] {
    type Apply[E <: Elem, V <: Value] <: Value
  }

  sealed trait Nat {
    type Match[NonZero[N <: Nat] <: Up, IfZero <: Up, Up] <: Up
    type Compare[N <: Nat] <: Comparison

    type FoldR[Init <: Type, Type, F <: Fold[Nat, Type]] <: Type
    type FoldL[Init <: Type, Type, F <: Fold[Nat, Type]] <: Type

    type Add[A <: Nat, B <: Nat]  = A#FoldR[B, Nat, Inc]
    type Inc = Fold[Nat, Nat] {
      type Apply[N <: Nat, Acc <: Nat] = Succ[Acc]
    }

    type Mult[A <: Nat, B <: Nat] = A#FoldR[_0, Nat, Sum[B]]
    type Sum[By <: Nat] = Fold[Nat, Nat] {
      type Apply[N <: Nat, Acc <: Nat] = Add[By, Acc]
    }

    type Fact[A <: Nat] = A#FoldL[_1, Nat, Prod]
    type Prod = Fold[Nat, Nat] {
      type Apply[N <: Nat, Acc <: Nat] = Mult[N, Acc]
    }

    type Exp[A <: Nat, B <: Nat] = B#FoldR[_1, Nat, ExpFold[A]]
    type ExpFold[By <: Nat] = Fold[Nat, Nat] {
      type Apply[N <: Nat, Acc <: Nat] = Mult[By, Acc]
    }

  }

  sealed trait _0 extends Nat {
    type Match[NonZero[_ <: Nat] <: Up, IfZero <: Up, Up] = IfZero

    type Compare[N <: Nat] = N#Match[ConstLT, EQ, Comparison]
    type ConstLT[A] = LT

    type FoldR[Init <: Type, Type, F <: Fold[Nat, Type]] = Init
    type FoldL[Init <: Type, Type, F <: Fold[Nat, Type]] = Init


  }
  sealed trait Succ[N <: Nat] extends Nat {
    type Match[NonZero[_ <: Nat] <: Up, IfZero <: Up, Up] = NonZero[N]

    type Compare[O <: Nat] = O#Match[N#Compare, GT, Comparison]

    type FoldR[Init <: Type, Type, F <: Fold[Nat, Type]] = F#Apply[Succ[N], N#FoldR[Init, Type, F]]
    type FoldL[Init <: Type, Type, F <: Fold[Nat, Type]] = N#FoldL[F#Apply[Succ[N], Init], Type, F]
  }

  type ConstFalse[T] = False
  type is0[that <: Nat] = Nat#Match[ConstFalse, True, Bool]


  sealed trait Comparison {
    type Match[IfLT <: Up, IfEQ <: Up, IfGT <: Up, Up] <: Up

    type gt = Match[False, False, True, Bool]
    type ge = Match[False, True, True, Bool]
    type eq = Match[False, True, False, Bool]
    type le = Match[True, True, False, Bool]
    type lt = Match[True, False, False, Bool]
  }

  sealed trait GT extends Comparison {
    type Match[IfLT <: Up, IfEQ <: Up, IfGT <: Up, Up] = IfGT
  }
  sealed trait LT extends Comparison {
    type Match[IfLT <: Up, IfEQ <: Up, IfGT <: Up, Up] = IfLT
  }
  sealed trait EQ extends Comparison {
    type Match[IfLT <: Up, IfEQ <: Up, IfGT <: Up, Up] = IfEQ
  }

  type _1 = Succ[_0]
  type _2 = Succ[_1]
  type _3 = Succ[_2]
  type _4 = Succ[_3]

  println(toBoolean[ _3#Compare[_4]#lt])
  println(toBoolean[ _3#Compare[_3]#le])
  println(toBoolean[ _4#Compare[_3]#le])

  type C = _0#FoldR[ Int, AnyVal, Fold[Nat, AnyVal]]
  implicitly[ C =:= Int ]

  /////////////////////////////////////////////////////////////////////////////
  implicitly[_4 =:= Nat#Add[_2, _2]]





















}
