import scala.concurrent._
import scala.concurrent.duration._
import akka.actor._


object MyHomeStep02 extends App {

	println("Welcome!")

	val system = ActorSystem("my_home")

	val father = system.actorOf(Props(Parent()), "andrea")
	//val mother = system.actorOf(Props(Parent()), "francesca")

	val marta = system.actorOf(Props(Baby()), "marta")
	//val teresa = system.actorOf(Props(Baby()), "teresa")

	val family = List(father)


object BabyMsgs {

	case object Cry

}

case class Baby() extends Actor with SchedulerImpl {
	import BabyMsgs._

	val hungry = every(3 hours)(doCry)
	val dirty = every(7 hours)(doCry)

	def doCry() = {
		println(s"${self.path} Uheee!")
		family.foreach{_ ! Cry}
	}

	def receive = {
		case _ => 
			println(s"${self.path} Uh?!")
	}
}

case class Parent() extends Actor {
	import BabyMsgs._

	def receive = {
		case Cry =>
			println(s"${self.path} Panic! ${sender.path} is crying!")
	}
}

}