import scala.concurrent._
import scala.concurrent.duration._
import akka.actor._


object MyHomeStep04 extends App {

	println("Welcome!")

	val system = ActorSystem("my_home")

	val father = system.actorOf(Props(Father()), "andrea")
	val mother = system.actorOf(Props(Mother()), "francesca")

	val marta = system.actorOf(Props(Baby()), "marta")
	//val teresa = system.actorOf(Props(Baby()), "teresa")

	val family = List(father, mother)


object BabyMsgs {

	case object Cry

	case object Milk
	case object Diaper

}

object ParentMsgs {

	case object TakeCare

}

case class Mother() extends Actor {
	import BabyMsgs._
	import ParentMsgs._

	def receive = {
		case Cry =>
			println(s"${self.path} Andrea take care!")
			father ! TakeCare
	}

}

case class Father() extends Actor with CoinThrower {
	import BabyMsgs._
	import ParentMsgs._	

	def receive = {
		case Cry =>
			println(s"${self.path} the mother will take care...")
		case TakeCare =>
			throwCoin match {
				case false => 
					println(s"${self.path} ok I will feed her")
					marta ! Milk
				case true => 
					println(s"${self.path} I'm sure she is dirty!")
					marta ! Diaper
			}
	}

}



case class Baby() extends Actor with SchedulerImpl {
	import BabyMsgs._
	import context._

	val hungry = every(3 hours){() => self ! GettingHungry}
	val dirty = every(7 hours){() => self ! GettingDirty}

	case class BabyStatus(dirty: Boolean = false, hungry: Boolean = false) {
		override def toString: String = 
			(dirty, hungry) match {
			case (false, false) => "WELL!! :-)"
			case (true, false) => "DIRTY!"
			case (false, true) => "HUNGRY!"
			case (true,true) => "VERY BAD!"
		}
	}

	case object GettingHungry
	case object GettingDirty

	def receive = receive(BabyStatus())

	def receive(status: BabyStatus):Receive = {
		println(s"${self.path} feel "+status);
		{
		case GettingDirty =>
			family.map(_ ! Cry)
			become(receive(BabyStatus(true, status.hungry)))
		case GettingHungry =>
			family.map(_ ! Cry)
			become(receive(BabyStatus(status.dirty, true)))			
		case Milk =>
			become(receive(BabyStatus(status.dirty, false)))	
		case Diaper =>
			become(receive(BabyStatus(false, status.hungry)))	
		case any =>
			println(s"You know, I'm a baby and I do not understand anything!!")
		}
	}

}

}