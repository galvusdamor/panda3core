package de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability

import java.io.FileInputStream

import de.uniulm.ki.panda3.symbolic.compiler.{ToPlainFormulaRepresentation, ClosedWorldAssumption}
import de.uniulm.ki.panda3.symbolic.parser.hddl.HDDLParser

/**
 * Created by dhoeller on 25.06.16.
 */
object PrintPlanningGraph {
  def main(args: Array[String]): Unit = {

    val domainFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest01_domain.hddl"
    val problemFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest01_problem.hddl"

    val parsedDomainAndProblem = HDDLParser.parseDomainAndProblem(new FileInputStream(domainFile), new FileInputStream(problemFile))
    val cwaAppliedDomainAndProblem = ClosedWorldAssumption.transform(parsedDomainAndProblem, ())
    val (domain, initialPlan) = ToPlainFormulaRepresentation.transform(cwaAppliedDomainAndProblem, ())
    val groundedInitialState = initialPlan.groundedInitialState filter {
      _.isPositive
    }
    val planningGraph = new GroundedPlanningGraph(domain, groundedInitialState.toSet, true, false, Left(Nil))

    writeNow("digraph G {\n")
    var i = 0

    for (eff <- planningGraph.initialState) {
      val litStr = eff.predicate.name + listToStr(eff.parameter)
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
          val litStr = prec.predicate.name + listToStr(prec.parameter)
          writeLater("  " + litStr + (i - 1) + " -> " + taskStr + i + ";\n")
        }
        for (add <- task.substitutedAddEffects) {
          val litStr = add.predicate.name + listToStr(add.parameter)
          writeLater("  " + taskStr + i + " -> " + litStr + (i) + ";\n")
        }
        for (del <- task.substitutedDelEffects) {
          val litStr = del.predicate.name + listToStr(del.parameter)
          writeLater("  " + taskStr + i + " -> " + litStr + (i) + " [style=dotted];\n")
        }
      }
      writeNow("  }\n") // of subgraph "action"

      for (lit <- layer._3) {
        val litStr = lit.predicate.name + listToStr(lit.parameter)
        writeNow("  node[label=\"" + litStr + "\"] " + litStr + i + ";\n")
      }
    }
    writeAll()
    writeNow("}\n") // of graph
  }

  def listToStr(list: Seq[Any]): String = {
    var str = ""
    for (e <- list) {
      str += "-"
      str += e
    }
    str
  }

  def writeNow(s: String): Unit = {
    print(s)
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