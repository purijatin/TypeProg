package reactives.akka

import akka.actor.{Cancellable, ActorSystem}
import akka.stream.{UniformFanInShape, ClosedShape, ActorMaterializer}
import akka.stream.scaladsl._
import org.reactivestreams.Subscriber

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Promise, Future}
import scala.util.{Failure, Success, Random}

object Prac {

  final case class Author(handle: String)

  final case class Hashtag(name: String)

  final case class Tweet(author: Author, timestamp: Long, body: String) {
    def hashtags: Set[Hashtag] =
      body.split(" ").collect { case t if t.startsWith("#") => Hashtag(t) }.toSet
  }

  val akka = Hashtag("#akka")


  implicit val system = ActorSystem("reactive-tweets")
  implicit val materializer = ActorMaterializer()

  def main(args: Array[String]) {
    run1
   // run2
    Thread.sleep(1000)
    system.shutdown()

  }

  def run2: Unit ={
    import scala.concurrent.ExecutionContext.Implicits._
    // An source that can be signalled explicitly from the outside
    val source: Source[Int, Promise[Option[Int]]] = Source.maybe[Int]

    // A flow that internally throttles elements to 1/second, and returns a Cancellable
    // which can be used to shut down the stream
    val flow: Flow[Int, Int, Cancellable] = ???

    // A sink that returns the first element of a stream in the returned Future
    val sink: Sink[Int, Future[Int]] = Sink.head[Int]

    // By default, the materialized value of the leftmost stage is preserved
    val r1: RunnableGraph[Promise[Option[Int]]] = source.via(flow).to(sink)

    // Simple selection of materialized values by using Keep.right
    val r2: RunnableGraph[Cancellable] = source.viaMat(flow)(Keep.right).to(sink)
    val r3: RunnableGraph[Future[Int]] = source.via(flow).toMat(sink)(Keep.right)

    // Using runWith will always give the materialized values of the stages added
    // by runWith() itself
    val r4: Future[Int] = source.via(flow).runWith(sink)
    val r5: Promise[Option[Int]] = flow.to(sink).runWith(source)
    val r6: (Promise[Option[Int]], Future[Int]) = flow.runWith(source, sink)

    // Using more complext combinations
    val r7: RunnableGraph[(Promise[Option[Int]], Cancellable)] =
      source.viaMat(flow)(Keep.both).to(sink)

    val r8: RunnableGraph[(Promise[Option[Int]], Future[Int])] =
      source.via(flow).toMat(sink)(Keep.both)

    val r9: RunnableGraph[((Promise[Option[Int]], Cancellable), Future[Int])] =
      source.viaMat(flow)(Keep.both).toMat(sink)(Keep.both)

    val r10: RunnableGraph[(Cancellable, Future[Int])] =
      source.viaMat(flow)(Keep.right).toMat(sink)(Keep.both)

    // It is also possible to map over the materialized values. In r9 we had a
    // doubly nested pair, but we want to flatten it out
    val r11: RunnableGraph[(Promise[Option[Int]], Cancellable, Future[Int])] =
      r9.mapMaterializedValue {
        case ((promise, cancellable), future) =>
          (promise, cancellable, future)
      }

    // Now we can use pattern matching to get the resulting materialized values
    val (promise, cancellable, future) = r11.run()

    // Type inference works as expected
    promise.success(None)
    cancellable.cancel()
    future.map(_ + 3)

    // The result of r11 can be also achieved by using the Graph API
    val r12: RunnableGraph[(Promise[Option[Int]], Cancellable, Future[Int])] =
      RunnableGraph.fromGraph(FlowGraph.create(source, flow, sink)((_, _, _)) { implicit builder =>
        (src, f, dst) =>
          import FlowGraph.Implicits._
          src ~> f ~> dst
          ClosedShape
      })
  }

  def run1: Unit = {
    def tweets: Source[Tweet, Unit] = Source.apply(
      Stream.range(0, 5, 1).map(x =>
        Tweet(Author(x + " - "), System.currentTimeMillis(), "body: " + x + " " + System.currentTimeMillis() + " #akka")))

    val writeAuthors: Sink[Author, Future[Unit]] = Sink.foreach(x => println(x + " " + Thread.currentThread()))
    val writeHashtags: Sink[Hashtag, Future[Unit]] = Sink.foreach(x => println(x + " " + Thread.currentThread()))

    val g = RunnableGraph.fromGraph(FlowGraph.create() { implicit b =>
      import FlowGraph.Implicits._

      val bcast = b.add(Broadcast[Tweet](3))
      tweets ~> bcast.in
      bcast.out(0) ~> Flow[Tweet].map(_.author) ~> writeAuthors
      bcast.out(1) ~> Flow[Tweet].mapConcat(_.hashtags.toList) ~> writeHashtags
      bcast.out(2) ~> Flow[Tweet].map(x => if (Random.nextBoolean()) 1 else sys.error("asfd")).fold(0)(_ + _) ~> Sink.foreach(println)
      ClosedShape
    })
//    val a = g.run()

    val balance = RunnableGraph.fromGraph(FlowGraph.create(){implicit  b =>
      import FlowGraph.Implicits._
      import scala.concurrent.ExecutionContext.Implicits._

      val bal = b.add(Balance[Tweet](3))
      tweets ~> bal.in
      bal.out(0) ~> Flow[Tweet].map(_.author) ~> writeAuthors
      bal.out(1) ~> Flow[Tweet].mapConcat(_.hashtags.toList) ~> writeHashtags
      bal.out(2) ~> Sink.foreachParallel(3)(println)
      ClosedShape
    })
    balance.run()

    val g2 = RunnableGraph.fromGraph(g = FlowGraph.create() { implicit builder: FlowGraph.Builder[Unit] =>
      import FlowGraph.Implicits._
      val in = Source(1 to 10)
      val out = Sink.foreach(println)

      val bcast = builder.add(Broadcast[Int](2))
      val merge = builder.add(Merge[Int](2))

      val f1, f2, f3, f4 = Flow[Int].map(_ + 10)

      in ~> f1 ~> bcast ~> f2 ~> merge ~> f3 ~> out
      bcast ~> f4 ~> merge
      ClosedShape
    })
    g2.run

    val topHeadSink = Sink.head[Int]
    val bottomHeadSink = Sink.head[Int]
    val sharedDoubler = Flow[Int].map(_ * 2)

    val g3 = RunnableGraph.fromGraph(FlowGraph.create(topHeadSink, bottomHeadSink)((_, _)) { implicit builder =>
      (topHS, bottomHS) =>
        import FlowGraph.Implicits._
        val broadcast = builder.add(Broadcast[Int](2))
        Source.single(1) ~> broadcast.in

        broadcast.out(0) ~> sharedDoubler ~> topHS.inlet
        broadcast.out(1) ~> sharedDoubler ~> bottomHS.inlet
        ClosedShape
    }).run()
    import scala.concurrent.ExecutionContext.Implicits._
    Future.sequence(Seq(g3._1, g3._2)) onComplete{
      case Success(x) => println("-> "+x)
      case Failure(y) => y.printStackTrace()
    }

    //////////////////////////////////////////////

    val pickMaxOfThree = FlowGraph.create() { implicit b =>
      import FlowGraph.Implicits._

      val zip1 = b.add(ZipWith[Int, Int, Int](math.max))
      val zip2 = b.add(ZipWith[Int, Int, Int](math.max))
      zip1.out ~> zip2.in0

      UniformFanInShape(zip2.out, zip1.in0, zip1.in1, zip2.in1)
    }

    val resultSink: Sink[Int, Future[Int]] = Sink.head[Int]

    val g4: RunnableGraph[Future[Int]] = RunnableGraph.fromGraph(FlowGraph.create(resultSink) { implicit b =>
      (sink: Sink[Int, Future[Int]]#Shape) =>
        import FlowGraph.Implicits._

        // importing the partial graph will return its shape (inlets & outlets)
        val pm3 = b.add(pickMaxOfThree)

        Source.single(10) ~> pm3.in(0)
        Source.single(2) ~> pm3.in(1)
        Source.single(3) ~> pm3.in(2)
        pm3.out ~> sink.inlet
        ClosedShape
    })

    for(i <- g4.run) println("g4: "+i)


    /////////////////////
    val sourceOne = Source(List(1,3))
    val sourceTwo = Source(List(2))
    val merged = Source.combine(sourceOne, sourceTwo)(Merge(_))

    val mergedResult: Future[Int] = merged.runWith(Sink.fold(0)(_ + _))
    for(i <- mergedResult) println("g5: "+i)
  }
}
