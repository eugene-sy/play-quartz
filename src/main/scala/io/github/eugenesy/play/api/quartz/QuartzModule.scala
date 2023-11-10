package io.github.eugenesy.play.api.quartz

import play.api.Configuration
import play.api.Environment
import play.api.inject.Binding
import play.api.inject.Module
import com.google.inject.Singleton

object QuartzModule {
  final val QuartzConfigurationKey = "play.quartz.config"
  final val WaitJobCompletionKey   = "waitJobCompletion"
}

@Singleton
final class QuartzModule extends Module {
  def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    Seq(bind[QuartzSchedulerApi].to[DefaultQuartzSchedulerApi].in[Singleton])
  }
}
