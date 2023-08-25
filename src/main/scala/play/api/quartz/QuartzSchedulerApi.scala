package play.api.quartz

import com.typesafe.config.Config
import org.quartz.impl.StdSchedulerFactory
import org.quartz.CronExpression
import org.quartz.CronScheduleBuilder
import org.quartz.CronTrigger
import org.quartz.Job
import org.quartz.JobDetail
import org.quartz.Scheduler
import org.quartz.SchedulerException
import org.quartz.SchedulerFactory
import org.quartz.SimpleScheduleBuilder
import org.quartz.Trigger
import org.quartz.TriggerBuilder
import play.api.inject.ApplicationLifecycle
import play.api.ConfigLoader
import play.api.Configuration
import play.api.Environment
import play.api.Logger

import java.util.Date
import javax.inject.Inject
import java.time.Duration
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success
import scala.util.Try

trait QuartzSchedulerApi {

  def schedulerFactory: SchedulerFactory
  def scheduler: Scheduler

  def start: Unit

  def stop: Unit

  @throws(classOf[SchedulerException])
  def scheduleWithInterval(jobDetails: JobDetail, interval: Duration): Date

  @throws(classOf[SchedulerException])
  def scheduleWithCron(jobDetails: JobDetail, expression: String): Date

  @throws(classOf[SchedulerException])
  def scheduleWithCron(jobDetails: JobDetail, expression: CronExpression): Date
}

final class DefaultQuartzSchedulerApi @Inject() (
    configuration: Configuration,
    lifecycle: ApplicationLifecycle
)(implicit executionContext: ExecutionContext)
    extends QuartzSchedulerApi {

  private val logger: Logger = Logger(classOf[DefaultQuartzSchedulerApi])
  private val quartzModuleConfiguration: QuartzModuleConfiguration =
    configuration.get[QuartzModuleConfiguration](QuartzModule.QuarzConfigrationKey)

  bootstrap()

  override def schedulerFactory: SchedulerFactory = new StdSchedulerFactory()

  override def scheduler: Scheduler = schedulerFactory.getScheduler

  override def start: Unit = scheduler.start()

  override def stop: Unit = scheduler.shutdown(quartzModuleConfiguration.waitJobCompletion)

  override def scheduleWithInterval(jobDetails: JobDetail, interval: Duration): Date = {
    val delay = interval.toMillis

    val trigger = TriggerBuilder
      .newTrigger()
      .startNow()
      .withSchedule(
        SimpleScheduleBuilder
          .simpleSchedule()
          .withIntervalInMilliseconds(delay)
          .repeatForever()
      )
      .build()
    scheduler.scheduleJob(jobDetails, trigger)
  }

  override def scheduleWithCron(jobDetails: JobDetail, expression: String): Date = {
    val trigger = TriggerBuilder
      .newTrigger()
      .startNow()
      .withSchedule(CronScheduleBuilder.cronSchedule(expression))
      .build()
    scheduler.scheduleJob(jobDetails, trigger)
  }

  override def scheduleWithCron(jobDetails: JobDetail, expression: CronExpression): Date = {
    val trigger = TriggerBuilder
      .newTrigger()
      .startNow()
      .withSchedule(CronScheduleBuilder.cronSchedule(expression))
      .build()
    scheduler.scheduleJob(jobDetails, trigger)
  }

  private def bootstrap(): Unit = {
    if (quartzModuleConfiguration.autostart) {
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

}

final case class QuartzModuleConfiguration(autostart: Boolean, waitJobCompletion: Boolean)

object QuartzModuleConfiguration {
  implicit val loader: ConfigLoader[QuartzModuleConfiguration] = (c: Config, path: String) => {
    val configBlock = c.getConfig(path)
    QuartzModuleConfiguration(
      configBlock.getBoolean("autostart"),
      configBlock.getBoolean("waitJobCompletion")
    )
  }
}
