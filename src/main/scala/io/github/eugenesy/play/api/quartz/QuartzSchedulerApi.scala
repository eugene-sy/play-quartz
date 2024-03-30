package io.github.eugenesy.play.api.quartz

import org.quartz.*
import org.quartz.impl.StdSchedulerFactory
import play.api.Configuration
import play.api.Logger
import play.api.inject.ApplicationLifecycle

import java.util.Date
import com.google.inject.Inject
import io.github.eugenesy.play.api.quartz.injectable.InjectableJobFactory

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.util.Failure
import scala.util.Success
import scala.util.Try

/**
 * Generic interface of the Quartz API instance. `QuartzSchedulerApi` provides access to Quartz scheduler factory and
 * scheduler. It also provides helper methods for job scheduling.
 *
 * You should not create the instance of `QuartzSchedulerApi` directly. Rather, you should rely on the dependency
 * injection.
 *
 * ===Example===
 *
 * Here is an example of how you can use the dependency injection to obtain an instance of `QuartzSchedulerApi`:
 * {{{
 *   class Application @Inject()(quartzApi: QuartzSchedulerApi) {
 *     // ...
 *   }
 * }}}
 */
trait QuartzSchedulerApi {

  /*
   * Returns an instance of Quarts `SchedulerFactory`
   *
   * It can be used to produce additional named schedulers or list all available schedulers
   *
   * ===Example===
   *
   * {{{
   *   class Application @Inject()(quartzApi: QuartzSchedulerApi) {
   *     val factory = quartzApi.schedulerFactory()
   *     val named = factory.getScheduler("named")
   *     factory.getAllSchedulers()
   *   }
   * }}}
   */
  def schedulerFactory: SchedulerFactory

  /*
   * Returns an instance of Quartz `Scheduler`
   */
  def scheduler: Scheduler

  /*
   * Starts the default scheduler
   *
   * @throws SchedulerException
   *   if scheduler shutdown was called or if there is an issue with the scheduler
   */
  @throws(classOf[SchedulerException])
  def start: Unit

  /*
   * Pauses the scheduler, the scheduler can be restarted afterwards
   *
   * @throws SchedulerException
   *   if scheduler shutdown was called or if there is an issue with the scheduler
   */
  @throws(classOf[SchedulerException])
  def standby: Unit

  /*
   * Permanently stops the scheduler. Depending on the configuration, it will wait for job completion
   *
   * @throws SchedulerException
   */
  @throws(classOf[SchedulerException])
  def stop: Unit

  /*
   * Permanently stops the scheduler without delay. Running jobs will be terminated without waiting for completion.
   *
   * @throws SchedulerException
   */
  @throws(classOf[SchedulerException])
  def stopNotWaiting: Unit

  /*
   * Schedules a job using default scheduler with a given interval.
   *
   * @throws SchedulerException
   *   if job cannot be added to the scheduler or if there is an internal scheduler error
   *
   * === Example ===
   * {{{
   *   class SimpleJob extends Job with Logging {
   *     @throws[JobExecutionException]
   *     override def execute(arg0: JobExecutionContext): Unit = {
   *       logger.error("This is a quartz job!")
   *     }
   *   }
   *
   *   class Application @Inject()(quartzApi: QuartzSchedulerApi) {
   *     quartzApi.scheduleWithCron(job, 1.minute)
   *   }
   * }}}
   */
  @throws(classOf[SchedulerException])
  def scheduleWithInterval(jobDetails: JobDetail, interval: FiniteDuration): Date

  /*
   * Schedules a job using default scheduler with a given cron string.
   *
   * @throws SchedulerException
   *   if job cannot be added to the scheduler,cron expression is not correct
   *   or if there is an internal scheduler error
   *
   * === Example ===
   * {{{
   *   class SimpleJob extends Job with Logging {
   *     @throws[JobExecutionException]
   *     override def execute(arg0: JobExecutionContext): Unit = {
   *       logger.error("This is a quartz job!")
   *     }
   *   }
   *
   *   class Application @Inject()(quartzApi: QuartzSchedulerApi) {
   *     quartzApi.scheduleWithCron(job, "* * 12 * * ?")
   *   }
   * }}}
   */
  @throws(classOf[SchedulerException])
  def scheduleWithCron(jobDetails: JobDetail, expression: String): Date

  /*
   * Schedules a job using default scheduler with a given cron expression.
   *
   * @throws SchedulerException
   *   if job cannot be added to the scheduler or if there is an internal scheduler error
   *
   * === Example ===
   * {{{
   *   class SimpleJob extends Job with Logging {
   *     @throws[JobExecutionException]
   *     override def execute(arg0: JobExecutionContext): Unit = {
   *       logger.error("This is a quartz job!")
   *     }
   *   }
   *
   *   class Application @Inject()(quartzApi: QuartzSchedulerApi) {
   *     quartzApi.scheduleWithCron(job, new CronExpression("* * 12 * * ?"))
   *   }
   * }}}
   */
  @throws(classOf[SchedulerException])
  def scheduleWithCron(jobDetails: JobDetail, expression: CronExpression): Date
}

final class DefaultQuartzSchedulerApi @Inject() (
    configuration: Configuration,
    jobFactory: InjectableJobFactory,
    lifecycle: ApplicationLifecycle
)(implicit executionContext: ExecutionContext)
    extends QuartzSchedulerApi {

  private val logger: Logger = Logger(classOf[DefaultQuartzSchedulerApi])
  private val quartzModuleConfiguration: QuartzModuleConfiguration =
    configuration.get[QuartzModuleConfiguration](QuartzModule.QuartzConfigurationKey)

  bootstrap()

  /*
   * Provides a singleton `SchedulerFactory` instance
   */
  override lazy val schedulerFactory: SchedulerFactory = new StdSchedulerFactory()

  /*
   * Provides the default singleton `Scheduler` instance
   */
  override lazy val scheduler: Scheduler = schedulerFactory.getScheduler

  override def start: Unit = scheduler.start()

  override def standby: Unit = scheduler.standby()

  override def stop: Unit = scheduler.shutdown(quartzModuleConfiguration.waitJobCompletion)

  override def stopNotWaiting: Unit = scheduler.shutdown(quartzModuleConfiguration.waitJobCompletion)

  override def scheduleWithInterval(jobDetails: JobDetail, interval: FiniteDuration): Date = {
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
      .forJob(jobDetails.getKey)
      .build()
    scheduler.scheduleJob(jobDetails, trigger)
  }

  override def scheduleWithCron(jobDetails: JobDetail, expression: String): Date = {
    val trigger = TriggerBuilder
      .newTrigger()
      .startNow()
      .withSchedule(CronScheduleBuilder.cronSchedule(expression))
      .forJob(jobDetails.getKey)
      .build()
    scheduler.scheduleJob(jobDetails, trigger)
  }

  override def scheduleWithCron(jobDetails: JobDetail, expression: CronExpression): Date = {
    val trigger = TriggerBuilder
      .newTrigger()
      .startNow()
      .withSchedule(CronScheduleBuilder.cronSchedule(expression))
      .forJob(jobDetails.getKey)
      .build()
    scheduler.scheduleJob(jobDetails, trigger)
  }

  private def bootstrap(): Unit = {
    logger.info("Starting Quartz Scheduler")
    scheduler.setJobFactory(jobFactory)
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
