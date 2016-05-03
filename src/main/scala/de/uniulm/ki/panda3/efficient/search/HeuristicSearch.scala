package de.uniulm.ki.panda3.efficient.search

import java.io.FileInputStream
import java.util
import java.util.concurrent.Semaphore

import de.uniulm.ki.panda3.efficient.Wrapping
import de.uniulm.ki.panda3.efficient.heuristic.{EfficientNumberOfFlaws, EfficientHeuristic}
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.modification.EfficientModification
import de.uniulm.ki.panda3.symbolic.compiler.pruning.PruneHierarchy
import de.uniulm.ki.panda3.symbolic.compiler.{ToPlainFormulaRepresentation, SHOPMethodCompiler, ClosedWorldAssumption}
import de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability.{LiftedForwardSearchReachabilityAnalysis, GroundedForwardSearchReachabilityAnalysis}
import de.uniulm.ki.panda3.symbolic.parser.hddl.HDDLParser
import de.uniulm.ki.panda3.symbolic.parser.xml.XMLParser
import de.uniulm.ki.panda3.symbolic.search.SearchNode
import de.uniulm.ki.util.Dot2PdfCompiler

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object HeuristicSearch {

  var outputPDF: String = "plan.pdf"

  def main(args: Array[String]) {
    /*if (args.length != 3) {
      println("This programm needs exactly three arguments\n\t1. the domain file\n\t2. the problem file\n\t3. the name of the output pdf")
      System.exit(1)
    }
    val domFile = args(0)
    val probFile = args(1)
    outputPDF = args(2)*/
    outputPDF = "/home/gregor/test.pdf"
    //val domFile = "/home/gregor/temp/model/domaineasy3.lisp"
    //val probFile = "/home/gregor/temp/model/problemeasy3.lisp"
    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/AssemblyTask_domain.xml"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/AssemblyTask_problem.xml"
    val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/SmartPhone-HierarchicalNoAxioms.xml"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_VeryVerySmall.xml"
    val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_VerySmall.xml"
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
    val groundedReachabilityAnalysis = GroundedForwardSearchReachabilityAnalysis(flattened._1, groundedInitialState.toSet)

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

    val domainToSearchWith = prunedDomain


    val wrapper = Wrapping(domainToSearchWith)
    val initialPlan = wrapper.unwrap(domainToSearchWith._2)

    println("done\nstart planner")

    System.in.read()
    //dfs(initialPlan, 0)
    val buildTree = false
    val heuristic = EfficientNumberOfFlaws
    val (searchNode, sem, _) = startSearch(initialPlan, wrapper, Some(2000000), buildTree, heuristic)

    sem.acquire()
    println("done")

    if (buildTree) {
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
  }

  def startSearch(initialPlan: EfficientPlan, wrapping: Wrapping, nodeLimit: Option[Int], buildTree: Boolean,
                  heuristic: EfficientHeuristic): (EfficientSearchNode, Semaphore, Unit => Unit) = {
    import scala.math.Ordering.Implicits._
    val semaphore: Semaphore = new Semaphore(0)
    val root = new EfficientSearchNode(initialPlan, null, Double.MaxValue)

    // variables for the search
    val initTime: Long = System.currentTimeMillis()
    var nodes: Int = 0 // count the nodes
    var d: Int = 0 // the depth
    var crap: Int = 0 // and how many dead ends we have encountered

    var abort = false

    val searchQueue = new mutable.PriorityQueue[(EfficientSearchNode, Int)]()
    //new util.ArrayDeque[(EfficientPlan, EfficientSearchNode, Int)]()
    var result: Option[EfficientPlan] = None
    searchQueue.enqueue((root, 0))

    var lowestHeuristicFound = Double.MaxValue
    var minFlaw = Integer.MAX_VALUE
    var total = 0


    def heuristicSearch(): (EfficientSearchNode, Option[EfficientPlan]) = {
      while (searchQueue.nonEmpty && result.isEmpty && nodeLimit.getOrElse(Int.MaxValue) >= nodes) {
        val (myNode, depth) = searchQueue.dequeue()
        val plan = myNode.plan
        val flaws = plan.flaws
        minFlaw = Math.min(minFlaw, flaws.length)

        //println("Queue size: "  + searchQueue.size)
        // heuristic statistics
        if (myNode.heuristic < lowestHeuristicFound) {
          lowestHeuristicFound = myNode.heuristic
          println("Found new lowest heuristic value: " + lowestHeuristicFound + " @ plan #" + nodes)
        }


        if (nodes % 300 == 0 && nodes > 0) {
          val nTime = System.currentTimeMillis()
          val nps = nodes.asInstanceOf[Double] / (nTime - initTime) * 1000
          println("Plans Expanded: " + nodes + " " + nps + " Queue size " + searchQueue.length + " Mods/plan " + total / nodes)
        }
        nodes += 1


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
            //val allmods = actualModifications map {_.longInfo} mkString("[",",","]")
            var modNum = 0
            while (modNum < actualModifications.length && result.isEmpty) {
              // apply modification
              //val newPlan: EfficientPlan = plan.modify(myNode.modifications(myNode.selectedFlaw)(modNum))
              val newPlan: EfficientPlan = plan.modify(actualModifications(modNum))

              if (newPlan.variableConstraints.potentiallyConsistent && newPlan.ordering.isConsistent) {
                val heuristicValue = newPlan.numberOfPlanSteps //heuristic.computeHeuristic(newPlan)
                //val modString = myNode.modHist + "\n" + flaws(myNode.selectedFlaw).longInfo + "\n" + actualModifications(modNum).longInfo + " (" + (smallFlawNumMod-1) + " alternatives)" +
                //"\n" + allmods
                val searchNode = if (buildTree) new EfficientSearchNode(newPlan, myNode, 0) else new EfficientSearchNode(newPlan, null, heuristicValue /*, modString*/)

                searchQueue enqueue ((searchNode, depth + 1))
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
        val (_, solution) = heuristicSearch()
        semaphore.release()
        solution match {
          case None       => println("No solution after visiting " + nodes + " search nodes")
          case Some(plan) =>
            val symPlan = wrapping.wrap(plan)
            Dot2PdfCompiler.writeDotToFile(symPlan, outputPDF)
            println("Found a solution after visiting " + nodes + " search nodes")
          //println(symPlan.longInfo)
          //println(symPlan.orderingConstraints.longInfo)
        }
      }
    }).start()

    (root, semaphore, { _ => abort = true })
  }
}
