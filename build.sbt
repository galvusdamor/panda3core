import sbtassembly.Plugin.AssemblyKeys._
import scoverage.ScoverageSbtPlugin

///////////////// start SBT plugins for code style and coverage
org.scalastyle.sbt.ScalastylePlugin.Settings

// activate HMTL highlighting
ScoverageSbtPlugin.ScoverageKeys.coverageHighlighting := false

//
assemblySettings


//// actual project

name := "panda3-core"

organization := "de.uni-ulm.ki"

version := "0.1.4"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"

libraryDependencies += "org.antlr" % "antlr4-runtime" % "4.5"

libraryDependencies += "org.scala-lang.modules" %% "scala-pickling" % "0.10.1"

org.scalastyle.sbt.PluginKeys.config := baseDirectory.value / "project" / "scalastyle_config.xml"

homepage := Some(url("http://www.uni-ulm.de/in/ki/staff/gregor-behnke.html"))

startYear := Some(2014)

description := "A planning system for partial-order causal-link, hierarchical and hybrid planning."

//licenses += "MIT" -> url("http://opensource.org/licenses/MIT")

scalaVersion := "2.11.6"

mainClass in assembly := Some("de.uniulm.ki.panda3.search.DFS")

compileOrder in Compile := CompileOrder.Mixed

compileOrder in Test := CompileOrder.Mixed

//scalacOptions ++= Seq("-Xelide-below","OFF")
