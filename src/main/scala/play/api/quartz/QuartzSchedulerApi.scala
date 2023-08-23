package play.api.quartz

import com.typesafe.config.Config
import org.quartz.impl.StdSchedulerFactory
import org.quartz.Scheduler
import org.quartz.SchedulerFactory
import play.api.inject.ApplicationLifecycle
import play.api.{ConfigLoader, Configuration, Environment, Logger}

import javax.inject.Inject
import java.time.Duration
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

trait QuartzSchedulerApi {

  def schedulerFactory: SchedulerFactory
  def scheduler: Scheduler

  def start: Unit

  def stop: Unit
}

final class DefaultQuartzSchedulerApi @Inject() (
    environment: Environment,
    configuration: Configuration,
    lifecycle: ApplicationLifecycle
)(implicit executionContext: ExecutionContext)
    extends QuartzSchedulerApi {

  private val logger: Logger = Logger(classOf[DefaultQuartzSchedulerApi])

  override def schedulerFactory: SchedulerFactory = new StdSchedulerFactory()

  override def scheduler: Scheduler = schedulerFactory.getScheduler

  override def start: Unit = scheduler.start()

  override def stop: Unit = scheduler.shutdown() // TODO: timeout

  logger.info(s"Configuration: $configuration")

  val quartzModuleConfig = configuration.get[QuartzModuleConfiguration](QuartzModule.QuarzConfigrationKey)

  if (quartzModuleConfig.autostart) {
    logger.info("Starting Quartz Scheduler")
    start
  }


  lifecycle.addStopHook { () =>
    Future {
      Try(stop) match {
        case Success(_) => logger.debug(s"Quartz Scheduler was successfully shut down.")
        case Failure(t) => logger.warn(s"Error occurred while shutting down Quartz Scheduler.", t)
      }
    }
  }
}

final case class QuartzModuleConfiguration(autostart: Boolean, shutdownTimeout: Duration)

object QuartzModuleConfiguration {
  implicit val loader: ConfigLoader[QuartzModuleConfiguration] = (c: Config, path: String) => {
    val configBlock = c.getConfig(path)
    QuartzModuleConfiguration(
      configBlock.getBoolean("autostart"),
      configBlock.getDuration("shutdownTimeout")
    )
  }
}