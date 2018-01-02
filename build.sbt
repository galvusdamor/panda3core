lazy val commonSettings = Seq(
                               name := "panda3-core",
                               description := "A planning system for partial-order causal-link, hierarchical and hybrid planning.",
                               homepage := Some(url("http://www.uni-ulm.de/in/ki/staff/gregor-behnke.html")),
                               organization := "de.uni-ulm.ki",
                               version := "0.2.1-SNAPSHOT",
                               scalaVersion := "2.12.3",
                               resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
                               libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test",
                               libraryDependencies += "org.antlr" % "antlr4-runtime" % "4.7",
                               startYear := Some(2014),
                               //licenses += "MIT" -> url("http://opensource.org/licenses/MIT")
                               compileOrder in Compile := CompileOrder.Mixed,
                               compileOrder in Test := CompileOrder.Mixed
                             )


//lazy val root = (project in file(".")).settings(commonSettings: _*)
lazy val panda3core = (project in file(".")).settings(commonSettings: _*)


lazy val assemblySettings = commonSettings ++ Seq(
                                                   scalaSource in Compile := file(panda3core.base.getAbsolutePath()) / "src" / "main" / "scala",
                                                   javaSource in Compile := file(panda3core.base.getAbsolutePath()) / "src" / "main" / "java",
                                                   resourceDirectory in Compile := file(panda3core.base.getAbsolutePath()) / "src" / "main" / "resources",
                                                   scalacOptions ++= Seq("-Xelide-below", "5000", "-Xdisable-assertions"),
                                                   test in assembly := {}
                                                 )


lazy val main = (project in (file("assembly") / "main")).settings(assemblySettings: _*).
  settings(
            target := file("assembly") / "main",
            assemblyJarName in assembly := "PANDA.jar",
            mainClass in assembly := Some("de.uniulm.ki.panda3.Main")
          )

/*lazy val csvExtractor = (project in (file("assembly") / "csvExtractor")).settings(assemblySettings: _*).
  settings(
            target := file("assembly") / "csvExtractor",
            assemblyJarName in assembly := "panda3csvExtractor.jar",
            mainClass in assembly := Some("de.uniulm.ki.util.collectPlanningInfo")
          )
*/

//mainClass in assembly := Some("de.uniulm.ki.panda3.translation.PANDAtranslator")
scalastyleConfig := baseDirectory.value / "project" / "scalastyle_config.xml"
