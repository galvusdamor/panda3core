package de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability

import de.uniulm.ki.panda3.symbolic.domain.datastructures.LayeredGroundedPrimitiveReachabilityAnalysis
import de.uniulm.ki.panda3.symbolic.domain.{Domain, ReducedTask, Task}
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask

import collection.mutable.{HashMap, MultiMap}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class GroundedPlanningGraph(domain: Domain, initialState: Set[GroundLiteral], computeMutexes: Boolean, isSerial: Boolean, disallowedTasks: Either[Seq[GroundTask], Seq[Task]]) extends
  LayeredGroundedPrimitiveReachabilityAnalysis {


  lazy val graphSize: Int = layerWithMutexes.size
  // This function should compute the actual planning graph
  override protected lazy val layer: Seq[(Set[GroundTask], Set[GroundLiteral])] = layerWithMutexes map { case (groundTasks, groundTaskMutexes, groundLiterals, groundLiteralMutexes) => (groundTasks, groundLiterals)
  }
  lazy val layerWithMutexes: Seq[(Set[GroundTask], Set[(GroundTask, GroundTask)], Set[GroundLiteral], Set[(GroundLiteral, GroundLiteral)])] = {

    def buildGraph(previousLayer: (Set[GroundTask], Set[(GroundTask, GroundTask)], Set[GroundLiteral], Set[(GroundLiteral, GroundLiteral)]),
                   addedPropositions: Set[GroundLiteral], deletedMutexes: Set[(GroundLiteral, GroundLiteral)]):
                   Seq[(Set[GroundTask], Set[(GroundTask, GroundTask)], Set[GroundLiteral], Set[(GroundLiteral, GroundLiteral)])] = {
      fillPreconMap(addedPropositions)
      //  Instantiate actions which become available because of new propositions and deletion of mutexes
      val newActions: Set[GroundTask] = addedPropositions ++ (deletedMutexes flatMap { mutex => Set(mutex._1, mutex._2) }) flatMap { groundLiteral => {
        domain.consumersOf(groundLiteral.predicate) flatMap { task =>
        createActionInstances(task, groundLiteral, task.precondition.conjuncts find { literal => literal.predicate == groundLiteral.predicate } get,
        task.precondition.conjuncts, previousLayer._4) } }
      }

      val allActions: Set[GroundTask] = previousLayer._1 ++ newActions
      /*
       * TODO: Try to shorten the expressions; check/filter unnecessary Pairs;
       */
      val newActionMutexes: Set[(GroundTask, GroundTask)] = (for (x <- allActions; y <- allActions) yield (x, y)) filter { case (gTask1, gTask2) => ((gTask1.substitutedDelEffects intersect
        (gTask2.substitutedAddEffects union gTask2.substitutedPreconditions)).isEmpty && (gTask2.substitutedDelEffects intersect
        (gTask1.substitutedAddEffects union gTask1.substitutedPreconditions)).isEmpty) ||
        (for(x <- gTask1.substitutedPreconditions; y <- gTask2.substitutedPreconditions) yield (x,y)).exists(previousLayer._4.contains(_))
      }
      val newPropositions: Set[GroundLiteral] = (newActions flatMap { nA => nA.substitutedAddEffects }) -- previousLayer._3
      /*
       * TODO: Think about a better way to compute proposition-mutexes.
       */
      val allPropositions: Set[GroundLiteral] = newPropositions ++ previousLayer._3
      val allPropositionMutexes: Set[(GroundLiteral, GroundLiteral)] = (for (x <- allPropositions; y <- allPropositions) yield (x, y)) filter { case (gLiteral1,gLiteral2) =>
          (for(x <- allActions filter { gTask => gTask.substitutedAddEffects contains gLiteral1 };
               y <- allActions filter { gTask => gTask.substitutedAddEffects contains gLiteral2 }) yield(x,y)) exists {
            gTaskPair =>  newActionMutexes.contains(gTaskPair) || newActionMutexes.contains(gTaskPair.swap)} }

      if (newPropositions.isEmpty && previousLayer._4.size == allPropositionMutexes.size) {
        Seq.empty[(Set[GroundTask], Set[(GroundTask, GroundTask)], Set[GroundLiteral], Set[(GroundLiteral, GroundLiteral)])]
      } else {
        val thisLayer = (allActions, newActionMutexes, allPropositions, allPropositionMutexes)
        thisLayer +: buildGraph(thisLayer, newPropositions, previousLayer._4 intersect allPropositionMutexes)
      }
    }

    def createActionInstances(task: ReducedTask, groundLiteral: GroundLiteral, literal: Literal, unsignedPrecons: Seq[Literal],
                              mutexes: Set[(GroundLiteral, GroundLiteral)], assignMap: Map[Variable, Constant] = Map(), groundLiterals: Seq[GroundLiteral] = Seq.empty[GroundLiteral]):
                              Set[GroundTask] = {
      val correct: Boolean = (literal.parameterVariables zip groundLiteral.parameter) forall { case (variable, constant) => assignMap.getOrElse(variable, constant) == constant }
      val updatedGroundLiterals = groundLiterals :+ groundLiteral
      val mutexFree: Boolean = (for(x <- updatedGroundLiterals; y <- updatedGroundLiterals) yield (x,y)) exists {
        potentialMutex => (mutexes contains potentialMutex) || (mutexes contains potentialMutex.swap )}
      if(correct && mutexFree) {
        val updatedAssignMap = assignMap ++ (literal.parameterVariables zip groundLiteral.parameter)
        val updatedPrecons = unsignedPrecons filterNot { _ == literal }
        if(updatedPrecons.isEmpty) {
          /*
           * TODO: Implement action initiation
           */
           Set(GroundTask(task, ???))
        } else {
          (updatedPrecons flatMap { literal => preconMap(literal.predicate) flatMap { potentialGroundLiteral =>
            createActionInstances(task, potentialGroundLiteral, literal, updatedPrecons, mutexes, updatedAssignMap, updatedGroundLiterals) } }).toSet
        }

      } else {
        Set.empty[GroundTask]
      }
    }

    def fillPreconMap(propositions: Set[GroundLiteral]): Unit = propositions.foreach(p => preconMap.addBinding(p.predicate, p))

    buildGraph((Set.empty[GroundTask], Set.empty[(GroundTask, GroundTask)], initialState, Set.empty[(GroundLiteral, GroundLiteral)]), initialState, Set.empty[(GroundLiteral, GroundLiteral)])
  }
  private val preconMap = new HashMap[Predicate, collection.mutable.Set[GroundLiteral]] with MultiMap[Predicate, GroundLiteral]
}
