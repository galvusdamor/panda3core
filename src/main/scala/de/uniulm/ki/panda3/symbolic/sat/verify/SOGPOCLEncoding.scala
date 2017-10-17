package de.uniulm.ki.panda3.symbolic.sat.verify

import de.uniulm.ki.panda3.configuration.SATReductionMethod
import de.uniulm.ki.panda3.symbolic.domain.{Task, Domain}
import de.uniulm.ki.panda3.symbolic.logic.Predicate
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.util._

import scala.collection.Seq
import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/**
  * This is an encoding that uses POCL criteria to ensure that the resulting plan is executable
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class SOGPOCLEncoding(timeCapsule: TimeCapsule, domain: Domain, initialPlan: Plan,
                           taskSequenceLengthQQ: Int, reductionMethod: SATReductionMethod, offsetToK: Int, overrideK: Option[Int] = None) extends SOGEncoding {
  lazy val taskSequenceLength: Int = taskSequenceLengthQQ

  protected def preconditionOfPath(path: Seq[Int], precondition: Predicate): String = "prec^" + path.mkString(";") + "_" + precondition.name

  protected def supporter(pathA: Seq[Int], pathB: Seq[Int], precondition: Predicate): String = "supp^" + pathA.mkString(";") + "_" + pathB.mkString(";") + "_" + precondition.name

  protected val before: ((Seq[Int], Seq[Int])) => String =
    memoise[(Seq[Int], Seq[Int]), String]({ case (pathA: Seq[Int], pathB: Seq[Int]) => "before_" + pathA.mkString(";") + "_" + pathB.mkString(";") })

  //protected def before(pathA: Seq[Int], pathB: Seq[Int]): String = "before_" + pathA.mkString(",") + "_" + pathB.mkString(",")


  override lazy val noAbstractsFormula: Seq[Clause] = primitivePaths flatMap { case (p, ts) => ts filter { _.isAbstract } map { t => Clause((pathAction(p.length - 1, p, t), false)) } }

  override lazy val stateTransitionFormula: Seq[Clause] = {
    println("Final SOG has " + rootPayload.ordering.vertices.length + " vertices")
    // assert correctness

    val pl: DirectedGraph[(Seq[Int], Set[Task])] = rootPayload.ordering.map(
      {
        case (path, tasks) =>
          val matchingPaths = primitivePaths.filter(_._1 == path)
          if (matchingPaths.isEmpty)
            (path, Set[Task]())
          else
            (path, matchingPaths.head._2)
      })

    /*rootPayload.ordering.vertices foreach { case (path, tasks) =>
      val matchingPaths = primitivePaths.filter(_._1 == path)
      if (matchingPaths.isEmpty)
        assert(tasks.isEmpty)
      else
        assert(matchingPaths.length == 1 && matchingPaths.head._2 == tasks)

    }*/

    print("Compute Transitive reducton ... ")
    //val sog = rootPayload.ordering.transitiveReduction
    val sog: DirectedGraph[(Seq[Int], Set[Task])] = pl.transitiveReduction
    println("done")

    /*println(sog.isAcyclic)

    val string = sog.dotString(options = DirectedGraphDotOptions(),
                               //nodeRenderer = {case (path, tasks) => tasks map { _.name } mkString ","})
                               nodeRenderer = {case (path, tasks) => tasks.count(_.isPrimitive) + " " + path})
    Dot2PdfCompiler.writeDotToFile(string, "sog.pdf")*/

    println("TREE P:" + primitivePaths.length + " S: " + taskSequenceLength)


    val initVertex = (-1 :: Nil, Set(initialPlan.init.schema))
    val goalVertex = (-2 :: Nil, Set(initialPlan.goal.schema))
    val extendedSOG = SimpleDirectedGraph(sog.vertices :+ initVertex :+ goalVertex, sog.edgeList ++ (sog.vertices flatMap { v => (initVertex, v) ::(v, goalVertex) :: Nil }) :+
      (initVertex, goalVertex))

    /*val stringA = extendedSOG.dotString(options = DirectedGraphDotOptions(),
                                        //nodeRenderer = {case (path, tasks) => tasks map { _.name } mkString ","})
                                        nodeRenderer = {case (path, tasks) => tasks.count(_.isPrimitive) + " " + path})
    Dot2PdfCompiler.writeDotToFile(stringA, "sogExt.pdf")*/


    // init and goal must be contaiend in the final plan
    val initAndGoalMustBePresent = Clause(pathAction(1, initVertex._1, initVertex._2.head)) :: Clause(pathAction(1, goalVertex._1, goalVertex._2.head)) :: Nil

    // for every present task, its preconditions must be supported
    val preconditionsMustBeSupportedTemp = extendedSOG.vertices flatMap { case node@(path, tasks) => tasks flatMap { t =>
      t.preconditionsAsPredicateBool map { case (prec, true) => (impliesSingle(pathAction(path.length, path, t), preconditionOfPath(path, prec)), (node, prec)) }
    }
    }

    val preconditionsMustBeSupported = preconditionsMustBeSupportedTemp map { _._1 }
    println("A " + preconditionsMustBeSupported.length)
    val preconditions = preconditionsMustBeSupportedTemp map { _._2 } distinct

    // if a precondition is supported it must be supported by some action that can actually support it ...
    val supportedPreconditionsMustHaveSupporterTemp = preconditions map { case (n@(path, _), prec) =>
      // go over all task that are potentially ordered before ..
      val excludedTasks = extendedSOG.reachable(n) + n

      val potentialSupportingTasks = extendedSOG.vertices filterNot excludedTasks flatMap { case supporter@(sPath, sTasks) =>
        sTasks filter { _.effectsAsPredicateBool exists { case (p, s) => s && p == prec } } map { t => (supporter, t) }
      }
      val supporterLiterals = potentialSupportingTasks map { _._1._1 } map { p => supporter(p, path, prec) }

      val supportedPrecMustHaveSupporter = impliesRightOr(preconditionOfPath(path, prec) :: Nil, supporterLiterals.distinct)
      // TODO add AMO constraint?
      val supporterMustBePresent: Seq[Clause] = (supporterLiterals zip potentialSupportingTasks).groupBy(_._2._1) map { case (supporter@(sPath, _), taskList) =>
        assert((taskList map { _._1 }).distinct.length == 1)
        val supportLiteral = taskList.head._1

        impliesRightOr(supportLiteral :: Nil, taskList map { _._2._2 } map { t => pathAction(sPath.length, sPath, t) })
      } toSeq

      (supporterMustBePresent :+ supportedPrecMustHaveSupporter, potentialSupportingTasks zip supporterLiterals map { x => (x._1._1._1, path, prec) })
    }

    val supportedPreconditionsMustHaveSupporter = supportedPreconditionsMustHaveSupporterTemp flatMap { _._1 }
    println("B " + supportedPreconditionsMustHaveSupporter.length)
    val supporterLiterals: Seq[(Seq[Int], Seq[Int], Predicate)] = supportedPreconditionsMustHaveSupporterTemp flatMap { _._2 } distinct

    // output supporter graph
    //val supporterGraph = SimpleDirectedGraph(extendedSOG.vertices map {_._1}, supporterLiterals map {case (a,b,_)=> (a,b)} distinct)
    //Dot2PdfCompiler.writeDotToFile(supporterGraph, "clgraph.pdf")
    //Dot2PdfCompiler.writeDotToFile(supporterGraph.condensation, "clgraph-condensation.pdf")

    val supportImpliesOrder = supporterLiterals map { case (p1, p2, prec) => impliesSingle(supporter(p1, p2, prec), before(p1, p2)) }
    println("C " + supportImpliesOrder.length)

    val pathsWithInitAndGoal = extendedSOG.vertices map { _._1 } toArray
    val onlyPathSOG = extendedSOG map { _._1 }

    var startTime = System.currentTimeMillis()
    val orderMustBeTransitive: Array[Clause] = {
      val clauses = new ArrayBuffer[Clause]
      var i = 0
      while (i < pathsWithInitAndGoal.length) {
        var k = 0
        while (k < pathsWithInitAndGoal.length) {
          if (!(onlyPathSOG.reachable(pathsWithInitAndGoal(i)) contains pathsWithInitAndGoal(k))) {
            var j = 0
            while (j < pathsWithInitAndGoal.length) {
              clauses append impliesRightAndSingle(before(pathsWithInitAndGoal(i), pathsWithInitAndGoal(j)) :: before(pathsWithInitAndGoal(j), pathsWithInitAndGoal(k)) :: Nil,
                                                   before(pathsWithInitAndGoal(i), pathsWithInitAndGoal(k)))
              j += 1
            }
          }
          k += 1
        }


        i += 1
      }

      clauses.toArray
    }
    /*      pathsWithInitAndGoal flatMap { i =>
            // only those which are not already ordered against it
            pathsWithInitAndGoal filterNot { k => onlyPathSOG.reachable(i) contains k } flatMap { k =>
              pathsWithInitAndGoal map { j => impliesRightAndSingle(before(i, j) :: before(j, k) :: Nil, before(i, k)) }
            }
          }*/
    var endTime = System.currentTimeMillis()
    println("D " + orderMustBeTransitive.length + " time needed " + (endTime - startTime).toDouble./(1000))

    val orderMustBeConsistent = pathsWithInitAndGoal flatMap { i => pathsWithInitAndGoal filter { _ != i } map { j => impliesNot(before(i, j), before(j, i)) } }
    println("E " + orderMustBeConsistent.length)

    val sogOrderMustBeRespected = extendedSOG.vertices flatMap { case p@(i, _) => extendedSOG.reachable(p).-(p) map { _._1 } map { j => Clause(before(i, j)) } }
    println("F " + sogOrderMustBeRespected.length)

    startTime = System.currentTimeMillis()
    // no causal threats
    val noCausalThreat: Seq[Clause] = supporterLiterals flatMap { case (p1, p2, prec) =>
      val nonOrdered = extendedSOG.vertices filterNot { case (p, _) => p == p1 || p == p2 || onlyPathSOG.reachable(p2).contains(p) || onlyPathSOG.reachable(p).contains(p1) }

      nonOrdered flatMap { case (pt, ts) =>
        val threadingTasks = ts filter { t => t.effectsAsPredicateBool exists { case (p, s) => !s && p == prec } }

        threadingTasks map { t => impliesRightOr(supporter(p1, p2, prec) :: pathAction(pt.length, pt, t) :: Nil, before(pt, p1) :: before(p2, pt) :: Nil) }

      }
    }
    endTime = System.currentTimeMillis()
    println("G " + noCausalThreat.length + " time needed " + (endTime - startTime).toDouble./(1000))


    initAndGoalMustBePresent ++ preconditionsMustBeSupported ++ supportedPreconditionsMustHaveSupporter ++
      supportImpliesOrder ++ orderMustBeTransitive ++ orderMustBeConsistent ++ sogOrderMustBeRespected ++
      noCausalThreat
  }

}