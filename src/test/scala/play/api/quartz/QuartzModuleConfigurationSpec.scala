package play.api.quartz

import com.typesafe.config.ConfigFactory
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.Configuration

import scala.util.Random

class QuartzModuleConfigurationSpec extends AnyWordSpec with Matchers {
  "QuartzModuleConfiguration" must {
    "read configuration object from config file" in {
      val wait = Random.nextBoolean()
      val configuration =
        s"""
           |play {
           |  quartz.config {
           |    waitJobCompletion = $wait
           |  }
           |}""".stripMargin
      val config = Configuration(ConfigFactory.parseString(configuration))

      config.get[QuartzModuleConfiguration](QuartzModule.QuartzConfigurationKey) mustBe QuartzModuleConfiguration(
        wait
      )
    }
  }
}
