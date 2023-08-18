package play.api.quartz

import play.api.Configuration
import play.api.Environment

import javax.inject.Singleton
import play.api.inject.Binding
import play.api.inject.Module

object QuartzModule {
  final val QuarzConfigrationKey = "play.quartz.config"
}

@Singleton
final class QuartzModule extends Module {
  def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    val config = configuration.underlying

    Seq(bind[QuartzSchedulerApi].to[DefaultQuartzSchedulerApi].in[Singleton])
  }
}
