package activemq

import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import javax.jms.Session

import org.apache.activemq.ActiveMQConnectionFactory

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

/**
  * Created by puri on 10/13/2016.
  */
object Producer {
  def main(args: Array[String]) {
//    BasicConfigurator.configure()

//    val exec = Executors.newFixedThreadPool(10)
    val exec = Executors.newCachedThreadPool()
    implicit val ctx = ExecutionContext.fromExecutor(exec)
    val body: Try[Unit] => Unit = {
      case Success(x) =>
      case Failure(e) => e.printStackTrace()
    }

    val count: Int = 100
//    Future {
//      for (i <- 1 to count) {
//        Future {
//          Producer.producer()
//        } onComplete body
//      }
//    }

    Future {
      for (i <- 1 to count) {
        Future {
          Consumer.consumer()
        } onComplete body
      }
    }

    Thread.sleep(5000)
//    exec.shutdown()
  }


  def producer(): Unit = {
    val connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616")
    val connection = connectionFactory.createConnection()

    try {
      connection.start()
      // JMS messages are sent and received using a Session. We will
      // create here a non-transactional session object. If you want
      // to use transactions you should set the first parameter to 'true'
      val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)

      val destination = session.createTopic("Topic.jatin")
      // MessageProducer is used for sending messages (as opposed
      // to MessageConsumer which is used for receiving them)
      val producer = session.createProducer(destination)
      // We will send a small text message saying 'Hello World!'
      val message = session.createTextMessage("Hello World! "+Thread.currentThread())
      // Here we are sending the message!
      producer.send(message)
//      System.out.println("Sent message '" + message.getText + "'")
      session.close()
      connection.close()
    } finally {
      connection.close()
    }
  }
}
