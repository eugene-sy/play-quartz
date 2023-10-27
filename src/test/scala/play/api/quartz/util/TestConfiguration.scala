package play.api.quartz.util

import play.api.Configuration
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.quartz.QuartzSchedulerApi

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
