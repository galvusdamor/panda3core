lazy val commonSettings = Seq(
                               name := "panda3-core",
                               description := "A planning system for partial-order causal-link, hierarchical and hybrid planning.",
                               homepage := Some(url("http://www.uni-ulm.de/in/ki/staff/gregor-behnke.html")),
                               organization := "de.uni-ulm.ki",
                               version := "0.2.0-SNAPSHOT",
                               scalaVersion := "2.11.8",
                               resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
                               libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.5" % "test",
                               libraryDependencies += "org.antlr" % "antlr4-runtime" % "4.5",
                               libraryDependencies += "org.sat4j" % "org.sat4j.core" % "2.3.1",
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
                                                   scalacOptions ++= Seq("-Xelide-below", "5000"),
                                                   test in assembly := {}
                                                 )


lazy val main = (project in (file("assembly") / "main")).settings(assemblySettings: _*).
  settings(
            target := file("assembly") / "main",
            assemblyJarName in assembly := "panda3main.jar",
            mainClass in assembly := Some("de.uniulm.ki.panda3.Main")
          )

// sbt pro/assembly
lazy val pro = (project in (file("assembly") / "pro")).settings(assemblySettings: _*).
  settings(
            target := file("assembly") / "pro",
            assemblyJarName in assembly := "panda3pro.jar",
            mainClass in assembly := Some("de.uniulm.ki.panda3.ProMain")
          )

lazy val sat = (project in (file("assembly") / "sat")).settings(assemblySettings: _*).
  settings(
            target := file("assembly") / "sat",
            assemblyJarName in assembly := "panda3sat.jar",
            mainClass in assembly := Some("de.uniulm.ki.panda3.symbolic.sat.verify.VerifyRunner")
          )


lazy val singleToMultiTLT = (project in (file("assembly") / "singleToMultiTLT")).settings(assemblySettings: _*).
  settings(
            target := file("assembly") / "singleToMultiTLT",
            assemblyJarName in assembly := "singleToMultiTLT.jar",
            mainClass in assembly := Some("de.uniulm.ki.panda3.planRecognition.singleToMultiTLT")
          )

lazy val switchTLT = (project in (file("assembly") / "switchTLT")).settings(assemblySettings: _*).
  settings(
            target := file("assembly") / "switchTLT",
            assemblyJarName in assembly := "tlt.jar",
            mainClass in assembly := Some("de.uniulm.ki.panda3.planRecognition.switchTLT")
          )

lazy val monroe = (project in (file("assembly") / "monroe")).settings(assemblySettings: _*).
  settings(
            target := file("assembly") / "monroe",
            assemblyJarName in assembly := "panda3monroe.jar",
            mainClass in assembly := Some("de.uniulm.ki.panda3.MonroeMain")
          )



lazy val csvExtractor = (project in (file("assembly") / "csvExtractor")).settings(assemblySettings: _*).
  settings(
            target := file("assembly") / "csvExtractor",
            assemblyJarName in assembly := "panda3csvExtractor.jar",
            mainClass in assembly := Some("de.uniulm.ki.util.collectPlanningInfo")
          )

lazy val transportProbGen = (project in (file("assembly") / "transportProbGen")).settings(assemblySettings: _*).
  settings(
            target := file("assembly") / "transportProbGen",
            assemblyJarName in assembly := "panda3transportProbGen.jar",
            mainClass in assembly := Some("de.uniulm.ki.panda3.problemGenerators.derivedFromSTRIPS.transport.transportProbGen")
          )


//mainClass in assembly := Some("de.uniulm.ki.panda3.translation.PANDAtranslator")
scalastyleConfig := baseDirectory.value / "project" / "scalastyle_config.xml"