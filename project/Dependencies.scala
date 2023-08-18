import sbt._

object Version {
  val play = _root_.play.core.PlayVersion.current
}

object Library {
  val playLogback        = "com.typesafe.play"  %% "play-logback"         % Version.play
  val playCore           = "com.typesafe.play"  %% "play"                 % Version.play
  val playScalaTest      = "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0-M6"
}

object Dependencies {
  val core = Seq(
    Library.playCore,
    Library.playLogback,
    Library.playScalaTest % Test
  )
}