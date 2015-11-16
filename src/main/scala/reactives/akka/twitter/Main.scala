package reactives.akka.twitter

import akka.actor.ActorSystem
import akka.stream.scaladsl._
import akka.stream.{ActorMaterializer, ClosedShape}
import org.reactivestreams.{Publisher, Subscriber}
import twitter4j._
import twitter4j.conf.Configuration

import scala.util.Random


object Main {
  val config: Configuration = new twitter4j.conf.ConfigurationBuilder()
    .setOAuthConsumerKey("QVPAWR9JFtVV5Qezb2mrLj0Zw")
    .setOAuthConsumerSecret("xJ8H6UHy5JgQwHqx9NG9tbC4c4PbIdIH1yejjfE9xwKZP2obD8")
    .setOAuthAccessToken("195712486-Nkte0xSS6Mv0BOG7wTD1w4HJ1kWfqWbS9pb6C7IT")
    .setOAuthAccessTokenSecret("jDcANIRqCq4tCbIClrgDKtqaEXIH12SYZsI5O9eyndDrC")
    .build

  val fact = new TwitterStreamFactory(config).getInstance()

  final case class Author(handle: String)

  final case class Hashtag(name: String)

  final case class Tweet(author: Author, timestamp: Long, body: String) {
    def hashtags: Set[Hashtag] =
      body.split(" ").collect { case t if t.startsWith("#") => Hashtag(t) }.toSet
    def contains(hash:String) = {
      hashtags.contains(Hashtag("#"+hash))
    }
  }

  val publisher = new Publisher[Status] {
    var ls = List.empty[Subscriber[_ >: Status]]
    override def subscribe(s: Subscriber[_ >: Status]): Unit = ls = s :: ls
    def noti(n:Status) = {
      ls.foreach(x => x.onNext(n))
    }
    def exit = ls.foreach(x => x.onComplete())
  }

  def main(args: Array[String]) {
    setup
    implicit val system = ActorSystem("reactive-tweets")
    implicit val materializer = ActorMaterializer()

    def tweets = Source.apply(publisher)
    def writeScala = Sink.foreach((x: Tweet) => println("Scala==> "+x))
    def writeJava = Sink.foreach((x: Tweet) => println("Java==> "+x))
    val fold: Flow[Tweet, Int, Unit] = Flow[Tweet].map(x => 1).fold(0)(_ + _)

    val g = RunnableGraph.fromGraph(FlowGraph.create() { implicit b =>
      import FlowGraph.Implicits._

      val bcast = b.add(Broadcast[Tweet](2))
      tweets.map(x => Tweet(Author(x.getUser.getName),x.getCreatedAt.getTime, x.getText)) ~> bcast.in
      val scala = bcast.out(0) ~> Flow[Tweet].filter(_.contains("scala"))
      val java = bcast.out(1) ~> Flow[Tweet].filter(_.contains("java"))

      val scalacast = b.add(Broadcast[Tweet](2))
      scala ~> scalacast.in

      scalacast.out(0).via(fold) ~> Sink.foreach((x:Int) => println("Scala Count: "+x))
      scalacast.out(1) ~> writeScala

      val javacast = b.add(Broadcast[Tweet](2))
      java ~> javacast
      javacast.out(0).via(fold) ~> Sink.foreach((x:Int) => println("Java Count: "+x))
      javacast.out(1) ~> writeJava

      ClosedShape
    })
    g.run()
    Thread.sleep(30000)
    println("-------exiting--------")
    publisher.exit
    fact.shutdown()
  }


  def setup: Unit = {
    fact.addListener(new StatusListener {
      override def onStallWarning(warning: StallWarning): Unit = ???
      override def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice): Unit = ???
      override def onScrubGeo(userId: Long, upToStatusId: Long): Unit = ???
      override def onStatus(status: Status): Unit = publisher.noti(status)
      override def onTrackLimitationNotice(numberOfLimitedStatuses: Int): Unit = ???
      override def onException(ex: Exception): Unit = ex.printStackTrace
    })
    fact.filter(new FilterQuery("#java"))
    fact.filter(new FilterQuery("#scala"))
  }
}
