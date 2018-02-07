package de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability

import java.io.{FileWriter, BufferedWriter, FileInputStream}

import de.uniulm.ki.panda3.symbolic.compiler.{ToPlainFormulaRepresentation, ClosedWorldAssumption}
import de.uniulm.ki.panda3.symbolic.logic.Constant
import de.uniulm.ki.panda3.symbolic.parser.hddl.HDDLParser
import de.uniulm.ki.util.Dot2PdfCompiler

/**
 * Created by dhoeller on 25.06.16.
 */
object PrintPlanningGraph {
  def main(args: Array[String]): Unit = {

    //    val domainFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest01_domain.hddl"
    //    val problemFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest01_problem.hddl"

    val domainFile = "/home/dhoeller/Dokumente/repositories/private/papers/2017-panda-pro/domains/simple-finite-domain.lisp.strips.3.pddl"
    val problemFile = "/home/dhoeller/Dokumente/repositories/private/papers/2017-panda-pro/domains/simple-finite-problem.lisp.strips.3.pddl"

    val parsedDomainAndProblem = HDDLParser.parseDomainAndProblem(new FileInputStream(domainFile), new FileInputStream(problemFile))
    val cwaAppliedDomainAndProblem = ClosedWorldAssumption.transform(parsedDomainAndProblem,(true,Set[String]()))
    val (domain, initialPlan) = ToPlainFormulaRepresentation.transform(cwaAppliedDomainAndProblem, ())
    val groundedInitialState = initialPlan.groundedInitialState filter {
      _.isPositive
    }
    val planningGraph = new GroundedPlanningGraph(domain, groundedInitialState.toSet, GroundedPlanningGraphConfiguration())
    println("LAYER " + planningGraph.layerWithMutexes.length)

    planningGraph.layerWithMutexes foreach { case (a, b, c, d) =>
      println("LAYER!!")
      //println(a mkString "  ;  ")
      //println(b map {case (a,b) => a.task.name + (a.arguments map {_.name}).mkString("(",",",")") + " -!- " +  b.task.name + (b.arguments map {_.name}).mkString("(",",",")")} mkString "\n")
      //println(c mkString "  ;  ")
      //println(d map {case (a,b) => a.predicate.name + (a.parameter map {_.name}).mkString("(",",",")") + " -!- " +  b.predicate.name + (b.parameter map {_.name}).mkString("(",",",")")} mkString "\n")
    }

    println(planningGraph.reachableGroundLiterals map {_.longInfo} mkString "\n")

    println(planningGraph.reachableGroundLiterals exists {_.predicate.name == "p"})
    println(planningGraph.reachableGroundLiterals exists {_.predicate.name == "g"})

    println(planningGraph.reachableGroundLiterals.length)
    println(planningGraph.reachableGroundPrimitiveActions.length)


    writeNow("digraph G {\n  rankdir=LR;\n")
    var i = 0

    for (eff <- planningGraph.initialState) {
      val litStr = (eff.predicate.name + listToStr(eff.parameter)).replaceAll("-", "_")
      writeNow("  node[label=\"" + litStr + "\"] " + litStr + 0 + ";\n")
    }

    for (layer <- planningGraph.layerWithMutexes) {
      i += 1

      writeNow("  subgraph cluster_" + i + "1 {\n")
      writeNow("    label = \"action-layer " + i + "\";\n")

      // print tasks
      for (task <- layer._1) {
        val taskStr = (task.task.name + listToStr(task.arguments)).replaceAll("-", "_")
        writeNow("    node[label=\"" + taskStr + "\"] " + taskStr + i + ";\n")
        for (prec <- task.substitutedPreconditions) {
          val litStr = (prec.predicate.name + listToStr(prec.parameter)).replaceAll("-", "_")
          writeLater("  " + litStr + (i - 1) + " -> " + taskStr + i + ";\n")
        }
        for (add <- task.substitutedAddEffects) {
          val litStr = (add.predicate.name + listToStr(add.parameter)).replaceAll("-", "_")
          writeLater("  " + taskStr + i + " -> " + litStr + (i) + ";\n")
        }
        for (del <- task.substitutedDelEffects) {
          val litStr = (del.predicate.name + listToStr(del.parameter)).replaceAll("-", "_")
          writeLater("  " + taskStr + i + " -> " + litStr + (i) + " [style=dotted];\n")
        }
      }
      writeNow("  }\n") // of subgraph "action"

      for (lit <- layer._3) {
        val litStr = (lit.predicate.name + listToStr(lit.parameter)).replaceAll("-", "_")
        writeNow("  node[label=\"" + litStr + "\"] " + litStr + i + ";\n")
      }
    }
    writeAll()
    writeNow("}\n") // of graph


    //Dot2PdfCompiler.writeDotToFile(dotString, "/home/dhoeller/Schreibtisch/testdot/pg.pdf")
    bw.close()
  }

  def listToStr(list: Seq[Any]): String = {
    var str = ""
    for (e <- list) {
      str += "-"
      str += (e match {
        case c: Constant => c.name;
        case x => x.toString
      })
    }
    str
  }

  var dotString: String = ""

  val bw = new BufferedWriter(new FileWriter("/home/dhoeller/Schreibtisch/testdot/pg2.dot"))

  def writeNow(s: String): Unit = {
    //dotString += s
    //print(s)
    bw.write(s)
  }

  var postponed: Seq[String] = Seq()

  def writeLater(s: String): Unit = {
    postponed = postponed :+ s
  }

  def writeAll(): Unit = {
    for (s <- postponed) {
      writeNow(s)
    }
  }
}