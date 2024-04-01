package io.github.eugenesy.play.api.quartz.injectable

import com.google.inject.Inject
import com.google.inject.Injector
import org.quartz.Job
import org.quartz.Scheduler
import org.quartz.simpl.SimpleJobFactory
import org.quartz.spi.TriggerFiredBundle

/**
 * Quartz job factory utilizing Guice dependency injection capabilities.
 *
 * Quartz jobs extending `InjectableJob` interface, provided by he plugin have their fields and setters annotated with
 * `@Inject` annotation are provided with their dependencies. The jobs extending the default Quartz interface or any
 * other interfaces, are left as is without injection.
 *
 * ===Example===
 * {{{
 *    class SimpleJob extends InjectableJob with Logging {
 *
 *      @Inject()
 *      var serviceA: ServiceA = null
 *
 *      var serviceB: ServiceB = null
 *
 *      @Inject()
 *      def setServiceB(b: ServiceB) = {
 *        this.serviceB = b
 *      }
 *
 *      @throws[JobExecutionException]
 *      override def execute(arg0: JobExecutionContext): Unit = {
 *        logger.error("This is a quartz job!")
 *      }
 *    }
 * }}}
 */
abstract class InjectableJobFactory extends SimpleJobFactory {

  def injector: Injector

  override def newJob(bundle: TriggerFiredBundle, Scheduler: Scheduler): Job = {
    val job = super.newJob(bundle, Scheduler)

    if (job.isInstanceOf[InjectableJob]) {
      injector.injectMembers(job)
      job
    } else {
      job
    }
  }
}

/**
 * Default implementation of the `InjectableJobFactory`.
 */
class SimpleInjectableJobFactory @Inject() (override val injector: Injector) extends InjectableJobFactory
