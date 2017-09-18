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
abstract class SOGPOCLEncoding extends SOGPartialNoPath {

  protected def preconditionOfPath(path: Seq[Int], precondition: Predicate): String = "prec^" + path.mkString(";") + "_" + precondition.name

  protected def deletes(path: Seq[Int], precondition: Predicate): String = "del^" + path.mkString(";") + "_" + precondition.name

  override lazy val stateTransitionFormula: Seq[Clause] = {

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
    println("Preconditions must supported: " + preconditionsMustBeSupported.length + " clauses")
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
    println("Supported preconditions must have supporter: " + supportedPreconditionsMustHaveSupporter.length + " clauses")
    val supporterLiterals: Seq[(Seq[Int], Seq[Int], Predicate)] = supportedPreconditionsMustHaveSupporterTemp flatMap { _._2 } distinct

    println("possible causal links: " + supporterLiterals.length)

    // output supporter graph
    //val supporterGraph = SimpleDirectedGraph(extendedSOG.vertices map {_._1}, supporterLiterals map {case (a,b,_)=> (a,b)} distinct)
    //Dot2PdfCompiler.writeDotToFile(supporterGraph, "clgraph.pdf")
    //Dot2PdfCompiler.writeDotToFile(supporterGraph.condensation, "clgraph-condensation.pdf")

    val supportImpliesOrder = supporterLiterals map { case (p1, p2, prec) => impliesSingle(supporter(p1, p2, prec), before(p1, p2)) }
    println("Support implies order: " + supportImpliesOrder.length + " clauses")


    var startTime = System.currentTimeMillis()
    val orderMustBeTransitive: Array[Clause] = transitiveOrderClauses
    var endTime = System.currentTimeMillis()
    println("Order is transitive and respects SOG: " + orderMustBeTransitive.length + " clauses, time needed " + (endTime - startTime).toDouble./(1000))

    val noCausalThreat :Seq[Clause] = causalThreatsFormula(supporterLiterals)

    initAndGoalMustBePresent ++ preconditionsMustBeSupported ++ supportedPreconditionsMustHaveSupporter ++
      supportImpliesOrder ++ orderMustBeTransitive ++ noCausalThreat
  }

  def causalThreatsFormula(supporterLiterals: Seq[(Seq[Int], Seq[Int], Predicate)]) : Seq[Clause]

}
