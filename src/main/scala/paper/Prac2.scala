package paper

/**
 *
 * Created by jatin on 28/6/15.
 */
object Prac2 {
  def main(args: Array[String]) {
    class Iter extends StringIterator("hi. wass up\n") with RichIterator
    val iter = new Iter
    iter foreach System.out.println

    class Iter2 extends StringIterator("string -2") with RichIterator with SyncIterator

    class Claz2 extends Claz with CB with CC with CD
    val cc = new Claz2
    println(cc.m)
  }
}

trait AbsIterator {
  type T
  def hasNext: Boolean
  def next: T
}

trait RichIterator extends AbsIterator {
  def foreach(f: T => Unit): Unit =
    while (hasNext) f(next)
}

class StringIterator(s: String) extends AbsIterator {
  type T = Char
  private var i = 0
  def hasNext = i < s.length()
  def next = { val x = s.charAt(i); i = i + 1; x }
}

trait SyncIterator extends AbsIterator {
  abstract override def hasNext: Boolean = synchronized(super.hasNext)
  abstract override def next: T = synchronized(super.next)
}

abstract class Graph {
  type Node <: BaseNode;

  class BaseNode { self: Node =>
    def connectWith(n: Node): Edge = new Edge(self, n);
  }

  class Edge(from: Node, to: Node) {
    def source() = from;

    def target() = to;
  }

}

class Hmm{
  def go: Unit ={
    val a = new Graph{
      type Node = BaseNode

      class Node1 extends BaseNode{

      }
    }

  }
}

trait CA{
   def m:Int
}

trait CB extends CA{
   abstract override def m:Int = {
     println("CB")
     super.m
   }
}

trait CC extends CA{ abstract override def m:Int = { println("CC"); super.m}}
trait CD extends CA{ abstract override def m:Int = { println("CD"); -1}}

class Claz extends CA{
   override def m: Int = 1
}




