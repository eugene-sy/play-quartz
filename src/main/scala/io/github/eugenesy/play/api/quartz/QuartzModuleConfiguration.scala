package io.github.eugenesy.play.api.quartz

import com.typesafe.config.Config
import play.api.ConfigLoader

/**
 * Configuration for the Quartz module
 * @param waitJobCompletion
 *   \- a flag defining if the quartz server will wait for jobs to be completed
 */
final case class QuartzModuleConfiguration(waitJobCompletion: Boolean)

object QuartzModuleConfiguration {
  implicit val loader: ConfigLoader[QuartzModuleConfiguration] = (c: Config, path: String) => {
    val configBlock = c.getConfig(path)
    QuartzModuleConfiguration(
      configBlock.getBoolean(QuartzModule.WaitJobCompletionKey)
    )
  }
}
