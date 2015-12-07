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

version := "0.1.1"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"

//homepage := Some(url("http://www.uni-ulm.de/in/ki/staff/thomas-geier.html"))

startYear := Some(2014)

//description := "Tools for probabilistic inference in discrete-valued factor graphs with dense factors."

//licenses += "MIT" -> url("http://opensource.org/licenses/MIT")

scalaVersion := "2.11.5"

mainClass in assembly := Some("de.uniulm.ki.panda3.search.DFS")