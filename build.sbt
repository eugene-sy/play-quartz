import scala.sys.process._

import com.typesafe.tools.mima.plugin.MimaPlugin._
import com.typesafe.tools.mima.core._
import interplay.ScalaVersions._

ThisBuild / resolvers ++= Resolver.sonatypeOssRepos("releases")

// Customise sbt-dynver's behaviour to make it work with tags which aren't v-prefixed
ThisBuild / dynverVTagPrefix := false

// Sanity-check: assert that version comes from a tag (e.g. not a too-shallow clone)
// https://github.com/dwijnand/sbt-dynver/#sanity-checking-the-version
Global / onLoad := (Global / onLoad).value.andThen { s =>
  dynverAssertTagVersion.value
  s
}

ThisBuild / playBuildRepoName := "play-slick"

lazy val commonSettings = Seq(
  // Work around https://issues.scala-lang.org/browse/SI-9311
  scalacOptions ~= (_.filterNot(_ == "-Xfatal-warnings")),
  scalaVersion       := "2.13.11",                   // scala213,
  crossScalaVersions := Seq("2.13.11", "3.3.1-RC5"), // scala213,
  pomExtra           := scala.xml.NodeSeq.Empty,     // Can be removed when dropping interplay
  developers += Developer(
    "playframework",
    "The Play Framework Contributors",
    "contact@playframework.com",
    url("https://github.com/playframework")
  ),
)

lazy val `play-quartz` = (project in file("."))
  .settings(libraryDependencies ++= Dependencies.core)
  .enablePlugins(PlayLibrary, Playdoc, MimaPlugin)
  .configs(Docs)

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.

val previousVersion: Option[String] = None

ThisBuild / mimaFailOnNoPrevious := false

def mimaSettings = Seq(
  mimaPreviousArtifacts := {
    if (scalaBinaryVersion.value == "3") {
      Set.empty // TODO
    } else {
      previousVersion.map(organization.value %% moduleName.value % _).toSet
    }
  }
)