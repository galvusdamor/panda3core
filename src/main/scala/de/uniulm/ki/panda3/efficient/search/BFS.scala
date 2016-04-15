package de.uniulm.ki.panda3.efficient.search


import java.io.FileInputStream
import java.util
import java.util.concurrent.Semaphore

import de.uniulm.ki.panda3.efficient.Wrapping
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.modification.EfficientModification
import de.uniulm.ki.panda3.symbolic.compiler.{SHOPMethodCompiler, ToPlainFormulaRepresentation, ClosedWorldAssumption}
import de.uniulm.ki.panda3.symbolic.parser.xml.XMLParser
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
    //val domFile = "/home/gregor/temp/send/domain2.lisp"
    //val probFile = "/home/gregor/temp/send/problem2.lisp"
    val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/AssemblyTask_domain.xml"
    val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/AssemblyTask_problem.xml"
    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/SmartPhone-HierarchicalNoAxioms.xml"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_VeryVerySmall.xml"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_VerySmall.xml"
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

    // wrap everything into the efficient datastructures
    val wrapper = Wrapping(flattened)
    val initialPlan = wrapper.unwrap(flattened._2)

    println("done\nstart planner")

    System.in.read()
    //dfs(initialPlan, 0)
    val (searchNode, sem, _) = startSearch(initialPlan, wrapper, Some(2000000), false)

    sem.acquire()
    println("done")

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
      //node.modifications // force the evaluation
      if (wrappC % 10 == 0) println("Wrapped: " + wrappC)
      node.children foreach { case (x, _) => dfsNode(x) }
    }

    dfsNode(symNode)
  }

  def startSearch(initialPlan: EfficientPlan, wrapping: Wrapping, nodeLimit: Option[Int], buildTree: Boolean): (EfficientSearchNode, Semaphore, Unit => Unit) = {
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
        minFlaw = Math.min(minFlaw, flaws.length)

        if (nodes % 500 == 0 && nodes > 0) {
          val nTime = System.currentTimeMillis()
          val nps = nodes.asInstanceOf[Double] / (nTime - initTime) * 1000
          //time = nTime
          println("Plans Expanded: " + nodes + " " + nps + " Depth " + depth + " Mods/plan " + total / nodes)
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
          if (buildTree) myNode.modifications = new Array[Array[EfficientModification]](flaws.length)
          var flawnum = 0
          myNode.selectedFlaw = 0
          var smallFlawNumMod = Integer.MAX_VALUE
          while (flawnum < flaws.length) {
            if (buildTree) {
              myNode.modifications(flawnum) = flaws(flawnum).resolver
              total += myNode.modifications(flawnum).length
              if (myNode.modifications(flawnum).length < smallFlawNumMod) {
                smallFlawNumMod = myNode.modifications(flawnum).length
                myNode.selectedFlaw = flawnum
              }
            } else {
              val numberOfModifiactions = flaws(flawnum).estimatedNumberOfResolvers
              total += numberOfModifiactions
              if (numberOfModifiactions < smallFlawNumMod) {
                smallFlawNumMod = numberOfModifiactions
                myNode.selectedFlaw = flawnum
              }
            }
            //assert(numberOfModifiactions == flaws(flawnum).resolver.length)
            flawnum += 1
          }
          // println("RESULT " + myNode.selectedFlaw + " @ " + smallFlawNumMod)

          val children = new ArrayBuffer[(EfficientSearchNode, Int)]()

          if (smallFlawNumMod != 0) {
            val actualModifications = if (buildTree) myNode.modifications(myNode.selectedFlaw) else flaws(myNode.selectedFlaw).resolver
            //assert(actualModifications.length == smallFlawNumMod, "Estimation of number of modifications was incorrect (" + actualModifications.length + " and " + smallFlawNumMod + ")")
            var modNum = 0
            while (modNum < actualModifications.length && result.isEmpty) {
              // apply modification
              //val newPlan: EfficientPlan = plan.modify(myNode.modifications(myNode.selectedFlaw)(modNum))
              val newPlan: EfficientPlan = plan.modify(actualModifications(modNum))

              if (newPlan.variableConstraints.potentiallyConsistent && newPlan.ordering.isConsistent) {
                val searchNode = if (buildTree) new EfficientSearchNode(newPlan, myNode, 0) else new EfficientSearchNode(newPlan, null, 0)

                if (buildTree) newPlan.flaws // force the new plan to compute its flaws

                stack add(newPlan, searchNode, depth + 1)
                children append ((searchNode, modNum))
              }
              modNum += 1
            }
          }
          if (buildTree) myNode.children = children.toArray
        }
        // now the node is processed
        myNode.dirty = false
      }
      semaphore.release()
      (root, result)
    }

    new Thread(new Runnable {
      override def run(): Unit = {
        val (_, solution) = bfs()
        semaphore.release()
        solution match {
          case None       => println("No solution")
          case Some(plan) =>
            val symPlan = wrapping.wrap(plan)
            println(symPlan.longInfo)
        }
      }
    }).start()

    (root, semaphore, { _ => abort = true })
  }
}