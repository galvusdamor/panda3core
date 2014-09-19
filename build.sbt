///////////////// SBT plugins for code style and coverage

resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"

resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "0.99.7.1")

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.5.0")

//org.scalastyle.sbt.ScalastylePlugin.Settings

//instrumentSettings


//// actual project

name := "panda3-core"

organization := "de.uni-ulm.ki"

version := "0.1.0"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"

//homepage := Some(url("http://www.uni-ulm.de/in/ki/staff/thomas-geier.html"))

startYear := Some(2014)

//description := "Tools for probabilistic inference in discrete-valued factor graphs with dense factors."

//licenses += "MIT" -> url("http://opensource.org/licenses/MIT")


scalaVersion := "2.11.2"

