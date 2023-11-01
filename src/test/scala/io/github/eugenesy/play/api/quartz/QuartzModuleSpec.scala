package io.github.eugenesy.play.api.quartz

import io.github.eugenesy.play.api.quartz.util.WithReferenceConfig
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.inject.guice.GuiceApplicationBuilder

class QuartzModuleSpec extends AnyWordSpec with Matchers {

  "reference.conf" must {
    "slick module is enabled" in new WithReferenceConfig {
      enabledModules(ref) must contain(classOf[QuartzModule].getName)
    }

    "provide a quartz config default path" in new WithReferenceConfig {
      val dbsKey = ref.getOptional[QuartzModuleConfiguration](QuartzModule.QuartzConfigurationKey)
      dbsKey mustBe Some(QuartzModuleConfiguration(waitJobCompletion = true))
    }
  }

  "QuartzModule" must {

    val appBuilder = GuiceApplicationBuilder()
    val injector   = appBuilder.injector()

    "bind QuartzSchedulerApi to DefaultQuartzSchedulerApi" in {
      val api = injector.instanceOf[QuartzSchedulerApi]
      api mustBe a[DefaultQuartzSchedulerApi]
    }

    "bind QuartzSchedulerApi as a singleton" in {
      val api1 = injector.instanceOf[QuartzSchedulerApi]
      val api2 = injector.instanceOf[QuartzSchedulerApi]
      api1 mustEqual api2
    }
  }
}
