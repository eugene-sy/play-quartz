package play.api.quartz

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.quartz.util.TestConfiguration.AutostartSUT
import play.api.quartz.util.TestConfiguration.NonAutostartSUT

class QuartzSchedulerApiAutostartSpec extends AnyWordSpec with Matchers with ScalaFutures {
  "QuartzSchedulerApi" must {
    "return an instance of Quartz scheduler" in new AutostartSUT {
      api.scheduler mustNot be(null)
    }

    "return an instance of Quartz scheduler factory" in new AutostartSUT {
      api.schedulerFactory mustNot be(null)
    }

    "return a started instance of scheduler in autostart configuration" in new AutostartSUT {
      api.scheduler.isStarted mustBe true
      api.scheduler.isInStandbyMode mustBe false
    }

    "change scheduler into standby mode" in new AutostartSUT {
      api.standby
      api.scheduler.isStarted mustBe true
      api.scheduler.isInStandbyMode mustBe true
    }
  }
}

class QuartzSchedulerApiNonAutostartSpec extends AnyWordSpec with Matchers {
  "QuartzSchedulerApi" must {
    // TODO: figure out how too restart app
    "return a stopped instance of scheduler in non-autostart configuration" ignore new NonAutostartSUT {
      api.scheduler.isStarted mustBe false
      api.scheduler.isInStandbyMode mustBe true
    }
  }
}
