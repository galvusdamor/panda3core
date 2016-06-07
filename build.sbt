name := "panda3-core"

organization := "de.uni-ulm.ki"

version := "0.2.0"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.5" % "test"

libraryDependencies += "org.antlr" % "antlr4-runtime" % "4.5"

libraryDependencies += "org.sat4j" % "org.sat4j.core" % "2.3.1"

scalastyleConfig := baseDirectory.value / "project" / "scalastyle_config.xml"

homepage := Some(url("http://www.uni-ulm.de/in/ki/staff/gregor-behnke.html"))

startYear := Some(2014)

description := "A planning system for partial-order causal-link, hierarchical and hybrid planning."

//licenses += "MIT" -> url("http://opensource.org/licenses/MIT")

scalaVersion := "2.11.7"

mainClass in assembly := Some("de.uniulm.ki.panda3.Main")
//mainClass in assembly := Some("de.uniulm.ki.panda3.translation.PANDAtranslator")


compileOrder in Compile := CompileOrder.Mixed

compileOrder in Test := CompileOrder.Mixed

//scalacOptions ++= Seq("-Xelide-below","OFF")
