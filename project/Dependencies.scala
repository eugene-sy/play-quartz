import sbt._

object Version {
  val play          = _root_.play.core.PlayVersion.current
  val playScalaTest = "6.0.0-M6"
  val quartz        = "2.3.2"
}

object Library {
  val playLogback   = "com.typesafe.play"      %% "play-logback"       % Version.play
  val playCore      = "com.typesafe.play"      %% "play"               % Version.play
  val playScalaTest = "org.scalatestplus.play" %% "scalatestplus-play" % Version.playScalaTest

  val quartz = "org.quartz-scheduler" % "quartz" % Version.quartz
}

object Dependencies {
  val core = Seq(
    Library.playCore,
    Library.playLogback,
    Library.quartz,
    Library.playScalaTest % Test
  )
}
