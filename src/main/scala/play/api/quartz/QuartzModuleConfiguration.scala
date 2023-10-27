package play.api.quartz

import com.typesafe.config.Config
import play.api.ConfigLoader

final case class QuartzModuleConfiguration(waitJobCompletion: Boolean)

object QuartzModuleConfiguration {
  implicit val loader: ConfigLoader[QuartzModuleConfiguration] = (c: Config, path: String) => {
    val configBlock = c.getConfig(path)
    QuartzModuleConfiguration(
      configBlock.getBoolean(QuartzModule.WaitJobCompletionKey)
    )
  }
}
