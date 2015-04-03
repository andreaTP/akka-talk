import scala.concurrent._
import scala.concurrent.duration._
import akka.actor._


object MyHomeStep01 extends App {

	println("Welcome!")

	val system = ActorSystem("my_home")

	val marta = system.actorOf(Props(Baby()), "marta")



case class Baby() extends Actor {

	def receive = {
		case _ => 
			println(s"${self.path} Uh?!")
	}
}

}