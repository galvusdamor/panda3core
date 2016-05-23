package de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability

import de.uniulm.ki.panda3.symbolic.domain.datastructures.{GroundedPrimitiveReachabilityAnalysis, GroundedReachabilityAnalysis, LayeredGroundedPrimitiveReachabilityAnalysis}
import de.uniulm.ki.panda3.symbolic.domain.{Domain, ReducedTask, Task}
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask

import collection.mutable.{HashMap, MultiMap}
import scala.collection.mutable

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class GroundedPlanningGraph(domain: Domain, initialState: Set[GroundLiteral], computeMutexes: Boolean, isSerial: Boolean, disallowedTasks: Either[Seq[GroundTask], Seq[Task]]) extends
  LayeredGroundedPrimitiveReachabilityAnalysis {


  lazy val graphSize: Int = ???
  val preconMap = new HashMap[Predicate, collection.mutable.Set[GroundLiteral]] with MultiMap[Predicate, GroundLiteral]


  // This function should compute the actual planning graph
  override protected lazy val layer: Seq[(Set[GroundTask], Set[GroundLiteral])] = {

    ???
  }

  protected lazy val layerWithMutexes: Seq[(Set[GroundTask], Set[(GroundTask, GroundTask)], Set[GroundLiteral], Set[(GroundLiteral, GroundLiteral)])] = {
    ???

    def buildGraph(layer: (Set[GroundTask], Set[(GroundTask, GroundTask)], Set[GroundLiteral], Set[(GroundLiteral, GroundLiteral)]), addedPropositions: Set[GroundLiteral], deletedMutexes: Set[(GroundLiteral, GroundLiteral)]): Seq[(Set[GroundTask], Set[(GroundTask, GroundTask)], Set[GroundLiteral], Set[(GroundLiteral, GroundLiteral)])] = {
      fillPreconMap(addedPropositions)

      val assignMap: Map[Variable, Constant] = Map()
      //  Instantiate actions which become available because of new propositions and deletion of mutexes
      val newActions: Set[GroundTask] = ((addedPropositions ++ (deletedMutexes flatMap { (t: (GroundLiteral, GroundLiteral)) => Set(t._1, t._2) })) flatMap { (gl: GroundLiteral) => {
        domain.consumersOf.getOrElse(gl.predicate, Seq.empty[ReducedTask]) flatMap { (t: ReducedTask) => createActionInstances(t, assignMap, gl, (t.precondition.conjuncts find { (l: Literal) => l.predicate == gl.predicate }).get, t.precondition.conjuncts, Seq.empty[GroundLiteral], layer._4) }
      }
      })
      val allActions: Set[GroundTask] = layer._1 ++ newActions
      /*
       * TODO: Try to shorten the expressions; check/filter unnecessary Pairs;
       */
      val newActionMutexes: Set[(GroundTask, GroundTask)] = allActions flatMap { (gt1: GroundTask) => allActions map { (gt2: GroundTask) => (gt1, gt2) } } filter { (gtPair: (GroundTask, GroundTask)) => ((gtPair._1.substitutedDelEffects intersect (gtPair._2.substitutedAddEffects union gtPair._2.substitutedPreconditions)).isEmpty && (gtPair._2.substitutedDelEffects intersect (gtPair._1.substitutedAddEffects union gtPair._1.substitutedPreconditions)).isEmpty) || (gtPair._1.substitutedPreconditions flatMap { (gt1: GroundLiteral) => gtPair._2.substitutedPreconditions map { (gt2: GroundLiteral) => (gt1, gt2) } } map { (glPair: (GroundLiteral, GroundLiteral)) => layer._4.contains(glPair) || layer._4.contains(glPair.swap) }).foldLeft(false)((b1: Boolean, b2: Boolean) => b1 || b2) }
      val newPropositions: Set[GroundLiteral] = (newActions flatMap { (nA: GroundTask) => nA.substitutedAddEffects }) -- layer._3
      /*
       * TODO: Think about a better way to compute proposition-mutexes.
       */
      val newPropositionMutexes: Set[(GroundLiteral, GroundLiteral)] = (newPropositions flatMap { (gl1: GroundLiteral) => (newPropositions ++ layer._3) map { (gl2: GroundLiteral) => (gl1, gl2) } }) filter { (glPair: (GroundLiteral, GroundLiteral)) => (((allActions filter { (gt: GroundTask) => gt.substitutedAddEffects contains (glPair._1) }) flatMap { (gt1: GroundTask) => (allActions filter { (gt: GroundTask) => gt.substitutedAddEffects contains (glPair._2) }) map { (gt2: GroundTask) => (gt1, gt2) } }) map { (gtPair: (GroundTask, GroundTask)) => ((layer._2 ++ newActionMutexes) contains (gtPair)) || ((layer._2 ++ newActionMutexes) contains (gtPair.swap)) }).foldLeft(false)((b1: Boolean, b2: Boolean) => b1 || b2) }
      val allPropositionMutexes: Set[(GroundLiteral, GroundLiteral)] = (((newPropositions ++ layer._3) flatMap { (gl1: GroundLiteral) => (newPropositions ++ layer._3) map { (gl2: GroundLiteral) => (gl1, gl2) } }) filter { (glPair: (GroundLiteral, GroundLiteral)) => (((allActions filter { (gt: GroundTask) => gt.substitutedAddEffects contains (glPair._1) }) flatMap { (gt1: GroundTask) => (allActions filter { (gt: GroundTask) => gt.substitutedAddEffects contains (glPair._2) }) map { (gt2: GroundTask) => (gt1, gt2) } }) map { (gtPair: (GroundTask, GroundTask)) => ((layer._2 ++ newActionMutexes) contains (gtPair)) || ((layer._2 ++ newActionMutexes) contains (gtPair.swap)) }).foldLeft(false)((b1: Boolean, b2: Boolean) => b1 || b2) })
      if (newPropositions.isEmpty && layer._4.size == allPropositionMutexes.size) {
        /*
         * TODO: Check if Nil is the correct return value.
         */
        Seq.empty[(Set[GroundTask], Set[(GroundTask, GroundTask)], Set[GroundLiteral], Set[(GroundLiteral, GroundLiteral)])]
      } else {
        val thisLayer = (allActions, newActionMutexes, (layer._3 ++ newPropositions), allPropositionMutexes)
        thisLayer +: buildGraph(thisLayer, newPropositions.toSet, layer._4 intersect allPropositionMutexes)
      }
    }
    /*
   * TODO: Check the correctness for special cases.
   */
    def createActionInstances(task: ReducedTask, assignMap: Map[Variable, Constant], gl: GroundLiteral, l: Literal, precons: Seq[Literal], gls: Seq[GroundLiteral], mutexes: Set[(GroundLiteral, GroundLiteral)]): Set[GroundTask] = {
      /*
       * TODO: Think about the necessity of gl matching
       */
      /*gl match {
        case null => Set.empty[GroundTask]
        case _ => {*/
      val correct: Boolean = ((l.parameterVariables zip gl.parameter) map { t: (Variable, Constant) => (assignMap.get(t._1) == t._2) }).foldLeft(false)((b1: Boolean, b2: Boolean) => b1 || b2)
      val mutexFree: Boolean = ((gls ++ Seq(gl)) flatMap {(gl1: GroundLiteral) => (gls ++ Seq(gl)) map { (gl2: GroundLiteral) => (gl1, gl2)}} map { (glPair: (GroundLiteral, GroundLiteral)) => (mutexes contains glPair) || (mutexes contains glPair.swap )}).foldLeft(false)((b1: Boolean, b2: Boolean) => b1 || b2)
      (correct && mutexFree) match {
        case true => {
          val updatedAssignMap = assignMap ++ (l.parameterVariables zip gl.parameter).toMap
          val updatedPrecons = (precons filterNot { (lit: Literal) => lit == l })
          updatedPrecons.size match {
            case 0 => {
              /*
              * TODO: Convert the updatedAssignMap back to a Seq[Constant] in the correct order.
              */
              Set(GroundTask(task, ???))
            }
            case _ => (updatedPrecons flatMap { (lit: Literal) => preconMap.getOrElse(lit.predicate, Set.empty[GroundLiteral]) flatMap { (newgl: GroundLiteral) => createActionInstances(task, updatedAssignMap, newgl, lit, updatedPrecons, (gls ++ Seq(gl)), mutexes) } }).toSet
          }
        }
        case false => Set.empty[GroundTask]
      }
      /*}
    }*/
    }

    def fillPreconMap(propositions: Set[GroundLiteral]): Unit = {
      propositions.foreach((p: GroundLiteral) => preconMap.addBinding(p.predicate, p))
    }

    buildGraph((Set.empty[GroundTask], Set.empty[(GroundTask, GroundTask)], initialState, Set.empty[(GroundLiteral, GroundLiteral)]), initialState, Set.empty[(GroundLiteral, GroundLiteral)])
  }
}
