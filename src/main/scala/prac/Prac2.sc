class P

class A {
  def m(p: P): A = { println("A.m"); this }
}

class B {
  def m(p: P): B = { println("B.m"); this }
}

def fill[S](p:P, n:{def m(p:P):S}):S = n.m(p)

fill(new P, new A)
trait HasM[S]{
  def m(p:P, a:S):S
}

object HasM{
  implicit def forA = new HasM[A] {
    override def m(p: P, a:A): A = a.m(p)
  }
}

def fill2[T](p:P, ob:T)(implicit fn:HasM[T]) = fn.m(p, ob)
fill2(new P, new A)