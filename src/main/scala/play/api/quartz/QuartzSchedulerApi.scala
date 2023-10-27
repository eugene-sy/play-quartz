package play.api.quartz

import org.quartz.*
import org.quartz.impl.StdSchedulerFactory
import play.api.Configuration
import play.api.Logger
import play.api.inject.ApplicationLifecycle

import java.time.Duration
import java.util.Date
import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success
import scala.util.Try

trait QuartzSchedulerApi {

  def schedulerFactory: SchedulerFactory

  def scheduler: Scheduler

  @throws(classOf[SchedulerException])
  def start: Unit

  @throws(classOf[SchedulerException])
  def standby: Unit

  @throws(classOf[SchedulerException])
  def stop: Unit

  @throws(classOf[SchedulerException])
  def stopNotWaiting: Unit

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
    configuration.get[QuartzModuleConfiguration](QuartzModule.QuartzConfigurationKey)

  bootstrap()

  override lazy val schedulerFactory: SchedulerFactory = new StdSchedulerFactory()

  override lazy val scheduler: Scheduler = schedulerFactory.getScheduler

  override def start: Unit = scheduler.start()

  override def standby: Unit = scheduler.standby()

  override def stop: Unit = scheduler.shutdown(quartzModuleConfiguration.waitJobCompletion)

  override def stopNotWaiting: Unit = scheduler.shutdown(quartzModuleConfiguration.waitJobCompletion)

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
    logger.info("Starting Quartz Scheduler")
    start

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
