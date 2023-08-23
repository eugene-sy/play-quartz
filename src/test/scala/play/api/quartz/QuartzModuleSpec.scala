package play.api.quartz

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.quartz.util.WithReferenceConfig

import java.time.Duration

class QuartzModuleSpec extends AnyWordSpec with Matchers {

  "reference.conf" must {
    "slick module is enabled" in new WithReferenceConfig {
      enabledModules(ref) must contain(classOf[QuartzModule].getName)
    }

    "provide a quartz config default path" in new WithReferenceConfig {
      val dbsKey = ref.getOptional[QuartzModuleConfiguration](QuartzModule.QuarzConfigrationKey)
      dbsKey mustBe Some(QuartzModuleConfiguration(autostart = true, shutdownTimeout = Duration.ofSeconds(5)))
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
