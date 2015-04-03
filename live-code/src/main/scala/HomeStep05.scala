import scala.concurrent._
import scala.concurrent.duration._
import akka.actor._


object MyHomeStep05 extends App {

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
		case TakeCare =>
			reandomImproper
			marta ! Milk
			marta ! Diaper

	}

	def reandomImproper = {
		scala.util.Random.nextInt(3) match {
			case 0 => println(s"******* Andrea you are never at home!!!You must do it!!!!!!!")
			case 1 => println(s"******* You are the father and you must spend time with her!!!!!!!")
			case 2 => println(s"******* I always take care of everything is your turn now!!!")
		}
	}

}

case class Father() extends Actor with CoinThrower {
	import BabyMsgs._
	import ParentMsgs._	
	import context._

	def receive = fresh

	def fresh: Receive = {
		println(s"${self.path} Now I feel better!");
		{
		case Cry =>
			println(s"${self.path} the mother will take care...")
		case TakeCare =>
			throwCoin match {
				case false => 
					marta ! Milk
					marta ! Diaper
				case true => 
					mother ! TakeCare
					become(tired())
			}
		}
	}

	val maxExasperation = 10

	def tired(exasperation: Int = maxExasperation): Receive = {
		println(s"${self.path} I'm really tired ${exasperation}");
		{
		case Cry =>
			println(s"${self.path} the mother will take care...")
		case TakeCare =>
			println(s"${self.path} Francesca take care!")
			mother ! TakeCare
			if (exasperation>0)
				become(tired(exasperation-1))
			else
				become(fresh)
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