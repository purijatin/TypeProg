package shapeprac

import java.net.URL

import shapeless._

object Prac2 extends App{
  val x: Nat = Nat(12)
  val y:_0 = null


  val url = new URL("http://google.com/")
  val conn = url.openConnection().getInputStream

  val arr = new Array[Byte](1)

  val st = System.currentTimeMillis()
  while(conn.read(arr) != -1){
      Thread.sleep(200)
  }
  println("over. "+(System.currentTimeMillis() - st))




}
