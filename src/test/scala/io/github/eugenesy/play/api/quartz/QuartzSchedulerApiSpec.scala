package io.github.eugenesy.play.api.quartz

import io.github.eugenesy.play.api.quartz.util.SimpleJob
import io.github.eugenesy.play.api.quartz.util.TestConfiguration.SUT
import org.quartz.CronExpression
import org.quartz.JobBuilder
import org.quartz.JobKey
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import io.github.eugenesy.play.api.quartz.util.TestConfiguration.SUT

import scala.concurrent.duration.DurationInt

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

    "schedule a job with interval provided" in new SUT {
      val jobGroup = "group1"
      val jobKey   = JobKey.jobKey("sample1", jobGroup)
      val job      = JobBuilder.newJob(classOf[SimpleJob]).withIdentity(jobKey).build()
      api.scheduleWithInterval(job, 1.minute)

      val details = api.scheduler.getJobDetail(jobKey)
      details.getJobClass mustBe classOf[SimpleJob]
      details.getKey mustBe jobKey
    }

    "schedule a job with cron string provided" in new SUT {
      val jobGroup = "group2"
      val jobKey   = JobKey.jobKey("sample2", jobGroup)
      val job      = JobBuilder.newJob(classOf[SimpleJob]).withIdentity(jobKey).build()
      api.scheduleWithCron(job, "* * 12 * * ?")

      val details = api.scheduler.getJobDetail(jobKey)
      details.getJobClass mustBe classOf[SimpleJob]
      details.getKey mustBe jobKey
    }

    "schedule a job with cron expression provided" in new SUT {
      val jobGroup = "group3"
      val jobKey   = JobKey.jobKey("sample3", jobGroup)
      val job      = JobBuilder.newJob(classOf[SimpleJob]).withIdentity(jobKey).build()
      api.scheduleWithCron(job, new CronExpression("* * 12 * * ?"))

      val details = api.scheduler.getJobDetail(jobKey)
      details.getJobClass mustBe classOf[SimpleJob]
      details.getKey mustBe jobKey
    }
  }
}
