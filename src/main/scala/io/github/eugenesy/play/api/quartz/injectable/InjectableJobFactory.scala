package io.github.eugenesy.play.api.quartz.injectable

import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.Singleton
import org.quartz.Job
import org.quartz.Scheduler
import org.quartz.simpl.SimpleJobFactory
import org.quartz.spi.TriggerFiredBundle

abstract class InjectableJobFactory(injector: Injector) extends SimpleJobFactory {
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

@Singleton
class SimpleInjectableJobFactory @Inject() (injector: Injector) extends InjectableJobFactory(injector)
