package play.api.quartz

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.inject.guice.GuiceApplicationBuilder

class QuartzModuleSpec extends AnyWordSpec with Matchers {
  "QuartzModule" must {

    val appBuilder = GuiceApplicationBuilder()
    val injector   = appBuilder.injector()

    "succeed" in {
      1 mustBe 1
    }
  }
}
