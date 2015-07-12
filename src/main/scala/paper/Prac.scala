package paper

/**
 * Created by jatin on 28/6/15.
 */
object Prac {
  def main(args: Array[String]) {
    val x:AbsCell = null
    val y: x.T = x.get


    var flip = false
    def f(): AbsCell = {
      flip = !flip
      if (flip) new AbsCell { type T = Int; val init = 1 }
      else new AbsCell { type T = String; val init = "" }
    }
    val x2 = f()

//    f().set(f().get)

  }

  def orderedPrac: Unit ={

  }
}

abstract class Ordered {
  type O
  def < (that: O): Boolean
  def <= (that: O): Boolean =  this < that || this == that
}

abstract class AbsCell {
  type T
  val init: T
  private var value: T = init
  def get: T = value
  def set(x: T): Unit = { value = x }
}

abstract class MaxCell extends AbsCell {
  type T <: Ordered { type O = T }
  def setMax(x: T) = if (get < x) set(x)
}

class C {
  protected var x = 0
  def incr: this.type = { x = x + 1; this }
}

class D extends C {
  def decr: this.type = { x = x - 1; this }
}

