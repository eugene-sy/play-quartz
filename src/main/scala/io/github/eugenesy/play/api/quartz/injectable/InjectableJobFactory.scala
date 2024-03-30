package io.github.eugenesy.play.api.quartz.injectable

import com.google.inject.Inject
import com.google.inject.Injector
import org.quartz.Job
import org.quartz.Scheduler
import org.quartz.simpl.SimpleJobFactory
import org.quartz.spi.TriggerFiredBundle

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

class SimpleInjectableJobFactory @Inject() (override val injector: Injector) extends InjectableJobFactory
