package io.github.eugenesy.play.api.quartz.util

import play.api.Configuration

trait WithReferenceConfig {
  val ref = Configuration.reference
  def enabledModules(c: Configuration): List[String] = {
    ref.get[Seq[String]]("play.modules.enabled").toList
  }
}
