package de.uniulm.ki.panda3.efficient.search

import java.io.FileInputStream
import java.util

import de.uniulm.ki.panda3.efficient.Wrapping
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.flaw.{EfficientCausalThreat, EfficientOpenPrecondition, EfficientAbstractPlanStep}
import de.uniulm.ki.panda3.efficient.plan.modification.{EfficientInsertPlanStepWithLink, EfficientModification}
import de.uniulm.ki.panda3.symbolic.compiler.{SHOPMethodCompiler, ToPlainFormulaRepresentation, ClosedWorldAssumption}
import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.parser.hddl.HDDLParser
import de.uniulm.ki.panda3.symbolic.parser.xml.XMLParser
import de.uniulm.ki.panda3.symbolic.plan.Plan

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object BFS {
  def main(args: Array[String]) {
    //if (args.length != 2) {
    //  println("This programm needs exactly two arguments\n\t1. the domain file\n\t2. the problem file")
    //  System.exit(1)
    //}
    //val domFile = args(0)
    //val probFile = args(1)
    val domFile = "/home/gregor/temp/panda3/domain2.lisp"
    val probFile = "/home/gregor/temp/panda3/problem2.lisp"
    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/AssemblyTask_domain.xml"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/AssemblyTask_problem.xml"
    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/SmartPhone-HierarchicalNoAxioms.xml"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_VerySmall.xml"
    print("Parsing domain and problem ...")
    val domAndInitialPlan: (Domain, Plan) = HDDLParser.parseDomainAndProblem(new FileInputStream(domFile), new FileInputStream(probFile))
    print("done\npreprocessing ...")
    val sortExpansion = domAndInitialPlan._1.expandSortHierarchy()

    val parsedDom = domAndInitialPlan._1.update(sortExpansion)
    val parsedProblem = domAndInitialPlan._2.update(sortExpansion)

    // apply the CWA
    val cwaApplied = ClosedWorldAssumption.transform(parsedDom, parsedProblem, ())
    val simpleMethod = SHOPMethodCompiler.transform(cwaApplied, ())
    val flattened = ToPlainFormulaRepresentation.transform(simpleMethod, ())
    print("done\ntransform to efficient representation ...")

    // wrap everything into the efficient Datastructures
    val wrapper = Wrapping(flattened)
    val initialPlan = wrapper.unwrap(flattened._2)

    println("done\nstart planner")

    //System.in.read()
    time = System.currentTimeMillis()
    //dfs(initialPlan, 0)
    val ret = bfs(initialPlan,wrapper)


    println("BFS finished with result: " + (if (ret.isDefined) "solvable" else "unsolvable"))
    if (ret.isDefined) {
      val symbolicPlan = wrapper.wrap(ret.get)
      //println(symbolicPlan)
      println(symbolicPlan.longInfo)
    }
  }

  var time: Long = 0


  def bfs(initialPlan: EfficientPlan, wrapping: Wrapping): Option[EfficientPlan] = {
    val stack = new util.ArrayDeque[EfficientPlan]()
    var result: Option[EfficientPlan] = None
    stack.add(initialPlan)

    var i = 0
    while (!stack.isEmpty && result.isEmpty) {
      if (i % 100 == 0 && i > 0) {
        val nTime = System.currentTimeMillis()
        val nps = i.asInstanceOf[Double] / (nTime - time) * 1000
        //time = nTime
        println("Plans Expanded: " + i) //  + " " + nps
      }
      i += 1
      val plan = stack.pop()
      val flaws = plan.flaws
      /*println("\n\nNEXT PLAN " + plan.hashCode() +  " - with " + flaws.length + " flaws")
      println(wrapping.wrap(plan).longInfo)
      println("Flaws:")
      flaws foreach {
        case EfficientAbstractPlanStep(_,ps) => println("ABSTRACT PLAN STEP: " + ps)
        case EfficientOpenPrecondition(_,ps,prec) => println("OPEN PRECONDITION: " + ps + " -> " + prec)
        case EfficientCausalThreat(_,_,ps,_,_) => println("CAUSAL THREAT: by " + ps)
        case x => println(x)
      }*/

      if (flaws.length == 0) {
        result = Some(plan)
      } else {
        val modifications = new Array[Array[EfficientModification]](flaws.length)
        var flawnum = 0
        var smallFlaw = 0
        var smallFlawNumMod = 0x3f3f3f3f
        while (flawnum < flaws.length) {
          //printTime("ToModcall")
          modifications(flawnum) = flaws(flawnum).resolver filterNot { _.isInstanceOf[EfficientInsertPlanStepWithLink] }
          //printTime("Modification")
          if (modifications(flawnum).length < smallFlawNumMod) {
            smallFlawNumMod = modifications(flawnum).length
            smallFlaw = flawnum
          }
          flawnum += 1
        }

        if (smallFlawNumMod != 0) {
          var modNum = 0
          while (modNum < smallFlawNumMod && result.isEmpty) {
            // apply modification
            val newPlan: EfficientPlan = plan.modify(modifications(smallFlaw)(modNum))
            if (newPlan.variableConstraints.potentiallyConsistent && newPlan.ordering.isConsistent) {
              stack add newPlan
            }
            modNum += 1
          }
        }
      }
    }
    result
  }
}