package io.github.eugenesy.play.api.quartz

import play.api.Configuration
import play.api.Environment
import play.api.inject.Binding
import play.api.inject.Module
import com.google.inject.Singleton
import io.github.eugenesy.play.api.quartz.injectable.InjectableJobFactory
import io.github.eugenesy.play.api.quartz.injectable.SimpleInjectableJobFactory

object QuartzModule {

  /** path in the **reference.conf** to obtain the plugin configuration */
  final val QuartzConfigurationKey = "play.quartz.config"

  /** configuration property for the graceful shutdown */
  final val WaitJobCompletionKey = "waitJobCompletion"
}

@Singleton
final class QuartzModule extends Module {
  def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] =
    Seq(
      bind[InjectableJobFactory].to[SimpleInjectableJobFactory].in[Singleton],
      bind[QuartzSchedulerApi].to[DefaultQuartzSchedulerApi].in[Singleton]
    )
}
