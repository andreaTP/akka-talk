import akka.actor._
import scala.concurrent._
import duration._

// To be hidden!!
trait SchedulerImpl {
	self: Actor =>
	import context.dispatcher

	def every(_d: FiniteDuration)(fn: () => Unit) = {
		val d = _d/60/60
		context.system.scheduler.schedule(d,d)(fn())
	}
}

trait CoinThrower {

	def throwCoin: Boolean =
		scala.util.Random.nextInt(10) > 5

}
