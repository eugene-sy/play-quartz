import sbt._

object Version {
  val play          = _root_.play.core.PlayVersion.current
  val playScalaTest = "6.0.0"
  val quartz        = "2.3.2"
  val guice         = "6.0.0"
}

object Library {
  val playLogback   = "com.typesafe.play"      %% "play-logback"       % Version.play
  val playCore      = "com.typesafe.play"      %% "play"               % Version.play
  val playScalaTest = "org.scalatestplus.play" %% "scalatestplus-play" % Version.playScalaTest

  val guice = "com.google.inject" % "guice" % Version.guice

  val quartz = "org.quartz-scheduler" % "quartz" % Version.quartz
}

object Dependencies {
  val core = Seq(
    Library.playCore,
    Library.playLogback,
    Library.guice,
    Library.quartz,
    Library.playScalaTest % Test
  )
}
