package de.uniulm.ki.panda3.efficient.search

import java.io.FileInputStream
import java.lang.AssertionError
import java.util
import java.util.concurrent.Semaphore

import de.uniulm.ki.panda3.efficient.Wrapping
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.flaw.{EfficientCausalThreat, EfficientOpenPrecondition, EfficientAbstractPlanStep}
import de.uniulm.ki.panda3.efficient.plan.modification.{EfficientDecomposePlanStep, EfficientInsertPlanStepWithLink, EfficientModification}
import de.uniulm.ki.panda3.symbolic.compiler.{SHOPMethodCompiler, ToPlainFormulaRepresentation, ClosedWorldAssumption}
import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.parser.hddl.HDDLParser
import de.uniulm.ki.panda3.symbolic.parser.xml.XMLParser
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.search.SearchNode

import scala.collection.mutable.ArrayBuffer

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object BFS {
  def main(args: Array[String]) {
    /*if (args.length != 2) {
      println("This programm needs exactly two arguments\n\t1. the domain file\n\t2. the problem file")
      System.exit(1)
    }
    val domFile = args(0)
    val probFile = args(1)*/
    //val domFile = "/home/gregor/temp/panda3/domain2.lisp"
    //val probFile = "/home/gregor/temp/panda3/problem2.lisp"
    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/AssemblyTask_domain.xml"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/AssemblyTask_problem.xml"
    val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/SmartPhone-HierarchicalNoAxioms.xml"
    val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_VeryVerySmall.xml"
    print("Parsing domain and problem ... ")
    //val domAndInitialPlan: (Domain, Plan) = HDDLParser.parseDomainAndProblem(new FileInputStream(domFile), new FileInputStream(probFile))
    val domAndInitialPlan = XMLParser.asParser.parseDomainAndProblem(new FileInputStream(domFile), new FileInputStream(probFile))
    print("done\npreprocessing ... ")
    val sortExpansion = domAndInitialPlan._1.expandSortHierarchy()

    val parsedDom = domAndInitialPlan._1.update(sortExpansion)
    val parsedProblem = domAndInitialPlan._2.update(sortExpansion)

    // apply the CWA
    val cwaApplied = ClosedWorldAssumption.transform(parsedDom, parsedProblem, ())
    val simpleMethod = SHOPMethodCompiler.transform(cwaApplied, ())
    val flattened = ToPlainFormulaRepresentation.transform(simpleMethod, ())
    print("done\ntransform to efficient representation ... ")

    // wrap everything into the efficient Datastructures
    val wrapper = Wrapping(flattened)
    val initialPlan = wrapper.unwrap(flattened._2)

    println("done\nstart planner")

    System.in.read()
    //dfs(initialPlan, 0)
    val (searchNode, _, _) = startSearch(initialPlan, wrapper, Some(1000000))


    /*println("BFS finished with result: " + (if (plan.isDefined) "solvable" else "unsolvable"))
    if (plan.isDefined) {
      val symbolicPlan = wrapper.wrap(plan.get)
      //println(symbolicPlan)
      println(symbolicPlan.longInfo)
    }*/
    System.in.read()
    val symNode = wrapper.wrap(searchNode)
    println("Start unwrapping")

    var wrappC = 0

    def dfsNode(node: SearchNode): Unit = {
      wrappC += 1
      if (wrappC % 10 == 0) println("Wrapped: " + wrappC)
      node.children foreach { case (x, _) => dfsNode(x) }
    }

    //dfsNode(symNode)
  }

  def startSearch(initialPlan: EfficientPlan, wrapping: Wrapping, nodeLimit: Option[Int]): (EfficientSearchNode, Semaphore, Unit => Unit) = {
    val semaphore: Semaphore = new Semaphore(0)
    val root = new EfficientSearchNode(initialPlan, null, Double.MaxValue)

    // variables for the search
    val initTime: Long = System.currentTimeMillis()
    var nodes: Int = 0 // count the nodes
    var d: Int = 0 // the depth
    var crap: Int = 0 // and how many dead ends we have encountered

    var abort = false

    val stack = new util.ArrayDeque[(EfficientPlan, EfficientSearchNode, Int)]()
    var result: Option[EfficientPlan] = None
    stack.add((initialPlan, root, 0))

    var lastDepth = -1
    var minFlaw = Integer.MAX_VALUE
    var total = 0


    def bfs(): (EfficientSearchNode, Option[EfficientPlan]) = {
      while (!stack.isEmpty && result.isEmpty && nodeLimit.getOrElse(Int.MaxValue) >= nodes) {
        val (plan, myNode, depth) = stack.pop()

        assert(depth >= lastDepth)
        if (depth != lastDepth) {
          println("Completed Depth " + lastDepth + " Minimal flaw count " + minFlaw)

          lastDepth = depth
          minFlaw = Integer.MAX_VALUE
        }

        val flaws = plan.flaws
        minFlaw = Math.min(minFlaw,flaws.length)

        if (nodes % 500 == 0 && nodes > 0) {
          val nTime = System.currentTimeMillis()
          val nps = nodes.asInstanceOf[Double] / (nTime - initTime) * 1000
          //time = nTime
          println("Plans Expanded: " + nodes + " " + nps + " Depth " + depth + " Mods/plan " + total/nodes)
        }
        nodes += 1

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
          myNode.modifications = new Array[Array[EfficientModification]](flaws.length)
          var flawnum = 0
          myNode.selectedFlaw = 0
          var smallFlawNumMod = Integer.MAX_VALUE
          //println("SEARCH ")
          while (flawnum < flaws.length) {
            //printTime("ToModcall")
            myNode.modifications(flawnum) = flaws(flawnum).resolver filterNot { _.isInstanceOf[EfficientInsertPlanStepWithLink] }
            //printTime("Modification")
            //println("MODS " + myNode.modifications(flawnum).length)
            total += myNode.modifications(flawnum).length
            if (myNode.modifications(flawnum).length < smallFlawNumMod) {
              smallFlawNumMod = myNode.modifications(flawnum).length
              myNode.selectedFlaw = flawnum
            }
            flawnum += 1
          }

          //println("RESULT " + myNode.selectedFlaw)

          //myNode.selectedFlaw = smallFlawNumMod

          val children = new ArrayBuffer[(EfficientSearchNode, Int)]()

          if (smallFlawNumMod != 0) {
            var modNum = 0
            while (modNum < smallFlawNumMod && result.isEmpty) {
              // apply modification
              val newPlan: EfficientPlan = plan.modify(myNode.modifications(myNode.selectedFlaw)(modNum))

              if (newPlan.variableConstraints.potentiallyConsistent && newPlan.ordering.isConsistent) {
                //val searchNode = new EfficientSearchNode(newPlan, myNode, 0)
                val searchNode = new EfficientSearchNode(newPlan, null, 0)
                // force the new plan to compute its flaws
                //newPlan.flaws


                stack add(newPlan, searchNode, depth + 1)
                children append ((searchNode, modNum))
              }
              modNum += 1
            }
          }

          //myNode.children = children.toArray
        }
        // now the node is processed
        myNode.dirty = false
      }
      semaphore.release()
      (root, result)
    }

    new Thread(new Runnable {
      override def run(): Unit = println(bfs())
    }).start()

    (root, semaphore, { _ => abort = true })
  }

}