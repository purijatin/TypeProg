sealed trait BoolType{
  type Not <: BoolType
  type Or[That <: BoolType] <: BoolType
}

sealed trait TrueType extends BoolType{
  override type Not = FalseType
  override type Or[That <: BoolType] = TrueType
}

sealed trait FalseType extends BoolType{
  override type Not = TrueType
  override type Or[That <: BoolType] = That
}

sealed trait IntVal{
  def plus(that:IntVal): IntVal
}

case object Int0 extends IntVal{
  override def plus(that:IntVal) = that
}

case class IntN(prev:IntVal) extends IntVal{
  override def plus(that:IntVal) = IntN(prev plus that)
}

val int1 = IntN(Int0)
val int2 = IntN(int1)

int1 plus Int0
int2 plus int1



