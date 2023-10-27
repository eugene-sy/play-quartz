package play.api.quartz

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.quartz.util.TestConfiguration.SUT

class QuartzSchedulerApiAutostartSpec extends AnyWordSpec with Matchers {
  "QuartzSchedulerApi" must {
    "return an instance of Quartz scheduler" in new SUT {
      api.scheduler mustNot be(null)
    }

    "return the same instance of Quartz scheduler when requested multiple times" in new SUT {
      api.scheduler mustBe api.scheduler
    }

    "return an instance of Quartz scheduler factory" in new SUT {
      api.schedulerFactory mustNot be(null)
    }

    "return the same instance of Quartz scheduler factory when requested multiple times" in new SUT {
      api.schedulerFactory mustBe api.schedulerFactory
    }

    "return a started instance of scheduler in autostart configuration" in new SUT {
      api.scheduler.isStarted mustBe true
      api.scheduler.isInStandbyMode mustBe false
    }

    "change scheduler into standby mode" in new SUT {
      api.standby
      api.scheduler.isStarted mustBe true
      api.scheduler.isInStandbyMode mustBe true
    }

    "change scheduler into stopped mode" in new SUT {
      api.stop
      api.scheduler.isStarted mustBe true
      api.scheduler.isInStandbyMode mustBe true
      api.scheduler.isShutdown mustBe true
    }
  }
}
