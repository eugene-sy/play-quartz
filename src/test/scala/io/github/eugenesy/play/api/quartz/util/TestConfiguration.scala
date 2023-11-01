package io.github.eugenesy.play.api.quartz.util

import io.github.eugenesy.play.api.quartz.QuartzSchedulerApi
import play.api.Configuration
import play.api.inject.guice.GuiceApplicationBuilder

object TestConfiguration {

  val moduleConfiguration = Configuration.from(
    Map(
      "play.quartz.config.waitJobCompletion" -> true
    )
  )

  trait SUT {
    lazy val appBuilder = GuiceApplicationBuilder(configuration = config)
    lazy val injector   = appBuilder.injector()
    lazy val api        = injector.instanceOf[QuartzSchedulerApi]
    lazy val config     = TestConfiguration.moduleConfiguration
  }
}
