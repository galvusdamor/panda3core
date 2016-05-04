package de.uniulm.ki.panda3.efficient.search


import java.io.FileInputStream
import java.util
import java.util.concurrent.Semaphore

import de.uniulm.ki.panda3.efficient.Wrapping
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.modification.EfficientModification
import de.uniulm.ki.panda3.symbolic.compiler.pruning.{PruneHierarchy, PruneTasks}
import de.uniulm.ki.panda3.symbolic.compiler.{SHOPMethodCompiler, ToPlainFormulaRepresentation, ClosedWorldAssumption}
import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability.{LiftedForwardSearchReachabilityAnalysis, GroundedForwardSearchReachabilityAnalysis}
import de.uniulm.ki.panda3.symbolic.parser.hddl.HDDLParser
import de.uniulm.ki.panda3.symbolic.parser.xml.XMLParser
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.search.SearchNode
import de.uniulm.ki.util.Dot2PdfCompiler

import scala.collection.mutable.ArrayBuffer

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object BFS {

  var outputPDF: String = "plan.pdf"

  def main(args: Array[String]) {
    /*if (args.length != 3) {
      println("This programm needs exactly three arguments\n\t1. the domain file\n\t2. the problem file\n\t3. the name of the output pdf")
      System.exit(1)
    }
    val domFile = args(0)
    val probFile = args(1)
    outputPDF = args(2)
    */

    //val domFile = "/media/dhoeller/Daten/Repositories/miscellaneous/A1-Vorprojekt/Planungsdomaene/verkabelung.lisp"
    //val probFile = "/media/dhoeller/Daten/Repositories/miscellaneous/A1-Vorprojekt/Planungsdomaene/problem1.lisp"
    val domFile = "/home/gregor/Workspace/panda2-system/domains/XML/UM-Translog/domains/UMTranslog.xml"
    val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/UM-Translog/problems/UMTranslog-P-1-Airplane.xml"

    //val domFile = "/home/gregor/temp/send/domain2.lisp"
    //val probFile = "/home/gregor/temp/send/problem2.lisp"
    //outputPDF = "/home/dhoeller/Schreibtisch/test.pdf"
    outputPDF = "/home/gregor/test.pdf"
    //val domFile = "/home/gregor/temp/model/domaineasy3.lisp"
    //val probFile = "/home/gregor/temp/model/problemeasy3.lisp"
    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/AssemblyTask_domain.xml"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/AssemblyTask_problem.xml"
    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/SmartPhone-HierarchicalNoAxioms.xml"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_VeryVerySmall.xml"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_VerySmall.xml"
    //val domFile = "/home/gregor/Dokumente/svn/miscellaneous/A1-Vorprojekt/Planungsdomaene/verkabelung.lisp"
    //val probFile = "/home/gregor/Dokumente/svn/miscellaneous/A1-Vorprojekt/Planungsdomaene/problem-test-split1.lisp"
    print("Parsing domain and problem ... ")
    //val domAndInitialPlan = HDDLParser.parseDomainAndProblem(new FileInputStream(domFile), new FileInputStream(probFile))
    val domAndInitialPlan = XMLParser.asParser.parseDomainAndProblem(new FileInputStream(domFile), new FileInputStream(probFile))
    print("done\npreprocessing ... ")
    val sortExpansion = domAndInitialPlan._1.expandSortHierarchy()

    val parsedDom = domAndInitialPlan._1.update(sortExpansion)
    val parsedProblem = domAndInitialPlan._2.update(sortExpansion)

    // apply the CWA
    val cwaApplied = ClosedWorldAssumption.transform(parsedDom, parsedProblem, ())
    val simpleMethod = SHOPMethodCompiler.transform(cwaApplied, ())
    val flattened = ToPlainFormulaRepresentation.transform(simpleMethod, ())

    print("done\nreducing the model ... ")
    println("\ncurrent domain:")
    println(flattened._1.statisticsString)

    val liftedRelaxedInitialState = flattened._2.init.schema.effectsAsPredicateBool
    val liftedReachabilityAnalysis = LiftedForwardSearchReachabilityAnalysis(flattened._1, liftedRelaxedInitialState.toSet)
    println("lifted analysis")
    println("" + liftedReachabilityAnalysis.reachableLiftedPrimitiveActions.size + " of " + flattened._1.primitiveTasks.size + " primitive tasks reachable")
    println("" + liftedReachabilityAnalysis.reachableLiftedLiterals.size + " of " + 2 * flattened._1.predicates.size + " lifted literals reachable")

    val groundedInitialState = flattened._2.groundedInitialState
    val groundedReachabilityAnalysis = GroundedForwardSearchReachabilityAnalysis(flattened._1, groundedInitialState.toSet)()

    println("grounded analysis")
    println("" + groundedReachabilityAnalysis.reachableLiftedPrimitiveActions.size + " of " + flattened._1.primitiveTasks.size + " primitive tasks reachable")
    println("" + groundedReachabilityAnalysis.reachableLiftedLiterals.size + " of " + 2 * flattened._1.predicates.size + " lifted literals reachable")

    val disallowedTasks = flattened._1.primitiveTasks filterNot groundedReachabilityAnalysis.reachableLiftedPrimitiveActions.contains
    val prunedDomain = PruneHierarchy.transform(flattened, disallowedTasks.toSet)

    println("reduced domain:")
    println(prunedDomain._1.statisticsString)

    //System.exit(0)
    print("transform to efficient representation ... ")

    // wrap everything into the efficient datastructures

    val domainToSearchWith = flattened //prunedDomain


    val wrapper = Wrapping(domainToSearchWith)
    val initialPlan = wrapper.unwrap(domainToSearchWith._2)

    println("done\nstart planner")

    //System.in.read()
    //dfs(initialPlan, 0)
    val buildTree = true
    val (searchNode, sem, _) = startSearch(initialPlan, wrapper, Some(2000000), buildTree)

    sem.acquire()
    println("done")

    if (buildTree) {
      val symNode = wrapper.wrap(searchNode)
      println("Start unwrapping")

      var wrappC = 0

      def dfsNode(node: SearchNode): Unit = {
        wrappC += 1
        node.modifications // force the evaluation
        if (wrappC % 10 == 0)
          println("Wrapped: " + wrappC)
        node.children foreach { case (x, _) => dfsNode(x) }
      }

      dfsNode(symNode)
    }
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
    var layerNumberOfNodes = 0
    var total = 0


    def bfs(): (EfficientSearchNode, Option[EfficientPlan]) = {
      while (!stack.isEmpty && result.isEmpty && nodeLimit.getOrElse(Int.MaxValue) >= nodes) {
        val (plan, myNode, depth) = stack.pop()

        //Dot2PdfCompiler.writeDotToFile(wrapping.wrap(plan), "/home/dhoeller/searchnode" + nodes +".pdf")

        assert(depth >= lastDepth)
        if (depth != lastDepth) {
          println("Completed Depth " + lastDepth + " Number Of Nodes: " + (nodes - layerNumberOfNodes) + " Minimal flaw count " + minFlaw)

          lastDepth = depth
          minFlaw = Integer.MAX_VALUE
          layerNumberOfNodes = nodes
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


        /*if (nodes == 1000){
          val symPlan = wrapping.wrap(plan)
          Dot2PdfCompiler.writeDotToFile(symPlan,"/home/gregor/test.pdf")
          System.exit(0)
        }*/
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
          case None       => println("No solution after visiting " + nodes + " search nodes")
          case Some(plan) =>
            val symPlan = wrapping.wrap(plan)
            Dot2PdfCompiler.writeDotToFile(symPlan, outputPDF)
            println("Found a solution after visiting " + nodes + " search nodes")
          //println(symPlan.longInfo)
        }
      }
    }).start()

    (root, semaphore, { _ => abort = true })
  }
}