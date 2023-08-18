package play.api.quartz

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.quartz.util.WithReferenceConfig

class QuartzModuleSpec extends AnyWordSpec with Matchers {

  "reference.conf" must {
    "slick module is enabled" in new WithReferenceConfig {
      enabledModules(ref) must contain(classOf[QuartzModule].getName)
    }
  }

  "QuartzModule" must {

    val appBuilder = GuiceApplicationBuilder()
    val injector   = appBuilder.injector()

    "bind QuartzSchedulerApi to DefaultQuartzSchedulerApi" in {
      val api = injector.instanceOf[QuartzSchedulerApi]
      api mustBe a[DefaultQuartzSchedulerApi]
    }
  }
}
