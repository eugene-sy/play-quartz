package play.api.quartz.util

import play.api.Configuration
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.quartz.QuartzSchedulerApi

object TestConfiguration {

  val autostartConfiguration = Configuration.from(
    Map(
      "play.quartz.config.autostart"         -> true,
      "play.quartz.config.waitJobCompletion" -> true
    )
  )

  val nonAutostartConfiguration = Configuration.from(
    Map(
      "play.quartz.config.autostart"         -> false,
      "play.quartz.config.waitJobCompletion" -> true
    )
  )

  trait SUT {
    def config: Configuration

    lazy val appBuilder = GuiceApplicationBuilder(configuration = config)
    lazy val injector   = appBuilder.injector()
    lazy val api        = injector.instanceOf[QuartzSchedulerApi]
  }

  trait AutostartSUT extends SUT {
    val config = TestConfiguration.autostartConfiguration
  }

  trait NonAutostartSUT extends SUT {
    val config = TestConfiguration.nonAutostartConfiguration
  }
}
