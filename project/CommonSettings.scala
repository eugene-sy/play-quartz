import sbt._

import sbt.Keys._

object CommonSettings extends AutoPlugin {

  override def requires = plugins.JvmPlugin
  override def trigger  = allRequirements

  override def projectSettings = Seq(
    Test / parallelExecution := false,
    Test / fork              := true
  )
}
