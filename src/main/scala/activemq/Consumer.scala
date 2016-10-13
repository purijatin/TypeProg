package activemq

import java.io.{BufferedReader, InputStreamReader}
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import javax.jms._

import org.apache.activemq.ActiveMQConnectionFactory

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
/**
  * Created by puri on 10/13/2016.
  */
object Consumer {
  def main(args: Array[String]) {
    // Create a ConnectionFactory
    val exec = Executors.newCachedThreadPool()
    implicit val ctx = ExecutionContext.fromExecutor(exec)
    val body: Try[Unit] => Unit = {
      case Success(x) =>
      case Failure(e) => e.printStackTrace()
    }
    Future {
      for (i <- 1 to 3) {
        Future {
          Consumer.consumer()
        } onComplete body
      }
    }
    
  }

  val co = new AtomicInteger(0)
  val id = new AtomicInteger(0)

  def consumer() = {
    val connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616")

    // Create a Connection
    val connection = connectionFactory.createConnection()
    val idNum: Int = id.incrementAndGet()
    connection.setClientID("consumer-"+idNum)
    connection.start()

    connection.setExceptionListener(new ExceptionListener {
      override def onException(exception: JMSException): Unit = exception.printStackTrace()
    })


    // Create a Session
    val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)

    // Create the destination (Topic or Queue)
    val destination = session.createTopic("Topic.jatin")

    val consumer: TopicSubscriber = session.createDurableSubscriber(destination,"myname")


    consumer.setMessageListener(new MessageListener {
      override def onMessage(message: Message): Unit = message match {
        case textMessage: TextMessage =>
          val text = textMessage.getText
          println("Received: " + text+" | idNum("+idNum+") | "+co.incrementAndGet())

        case x =>  println("Received: " + x)
      }
    })


    // Create a MessageConsumer from the Session to the Topic or Queue
//    val consumer = session.createConsumer(destination)


    // Wait for a message
//    val message = consumer.receive(1000000)
    new BufferedReader(new InputStreamReader(System.in)).read()
    println(Thread.currentThread()+" shutting down!")
    consumer.close()
    session.close()
    connection.close()
  }
}
