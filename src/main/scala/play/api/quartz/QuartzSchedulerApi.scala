package play.api.quartz

import org.quartz.impl.StdSchedulerFactory
import org.quartz.Scheduler
import org.quartz.SchedulerFactory
import play.api.inject.ApplicationLifecycle
import play.api.Configuration
import play.api.Environment

import javax.inject.Inject
import scala.concurrent.ExecutionContext

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
  override def schedulerFactory: SchedulerFactory = new StdSchedulerFactory()

  override def scheduler: Scheduler = schedulerFactory.getScheduler

  override def start: Unit = scheduler.start()

  override def stop: Unit = scheduler.shutdown() // TODO: timeout
}
