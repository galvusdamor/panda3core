package de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability

import de.uniulm.ki.panda3.symbolic.csp.VariableConstraint
import de.uniulm.ki.panda3.symbolic.domain.datastructures.LayeredGroundedPrimitiveReachabilityAnalysis
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


  lazy val graphSize: Int = layerWithMutexes.size
  // This function should compute the actual planning graph
  override protected lazy val layer: Seq[(Set[GroundTask], Set[GroundLiteral])] = layerWithMutexes map { case (groundTasks, groundTaskMutexes, groundLiterals, groundLiteralMutexes) => (groundTasks, groundLiterals) }

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
      val newNoOps: Set[GroundTask] = addedPropositions map { groundLiteral => createNOOP(groundLiteral)}

      val allActions: Set[GroundTask] = previousLayer._1 ++ newActions ++ newNoOps
      /*
       * TODO: Try to shorten the expressions; check/filter unnecessary Pairs;
       */
      val newActionMutexes: Set[(GroundTask, GroundTask)] = (for (x <- allActions; y <- allActions) yield (x, y)) filter { case (gTask1, gTask2) => (gTask1.substitutedDelEffects intersect
        (gTask2.substitutedAddEffects union gTask2.substitutedPreconditions)).nonEmpty ||
        (for(x <- gTask1.substitutedPreconditions; y <- gTask2.substitutedPreconditions) yield (x,y)).exists(previousLayer._4.contains)
      }
      val newPropositions: Set[GroundLiteral] = (newActions flatMap { newAction => newAction.substitutedAddEffects }) -- previousLayer._3
      /*
       * TODO: Think about a better way to compute proposition-mutexes.
       */
      val allPropositions: Set[GroundLiteral] = newPropositions ++ previousLayer._3
      val allPropositionMutexes: Set[(GroundLiteral, GroundLiteral)] = (for (x <- allPropositions; y <- allPropositions) yield (x, y)) filter { case (gLiteral1,gLiteral2) =>
          (for(x <- allActions filter { gTask => gTask.substitutedAddEffects contains gLiteral1 };
               y <- allActions filter { gTask => gTask.substitutedAddEffects contains gLiteral2 }) yield(x,y)) exists {
            gTaskPair =>  newActionMutexes(gTaskPair) || newActionMutexes(gTaskPair.swap)} }

      val thisLayer = (allActions, newActionMutexes, allPropositions, allPropositionMutexes)
      if (newPropositions.isEmpty && previousLayer._4.size == allPropositionMutexes.size) {
        Seq(thisLayer)
      } else {
        thisLayer +: buildGraph(thisLayer, newPropositions, previousLayer._4 diff allPropositionMutexes)
      }
    }

    def createActionInstances(task: ReducedTask, groundLiteral: GroundLiteral, literal: Literal, unassignedPrecons: Seq[Literal],
                              mutexes: Set[(GroundLiteral, GroundLiteral)], assignMap: Map[Variable, Constant] = Map(), groundLiterals: Seq[GroundLiteral] = Seq.empty[GroundLiteral]):
                              Set[GroundTask] = {
      val updatedPrecons: Seq[Literal] = unassignedPrecons filterNot { _ == literal }
      val updatedGroundLiterals = groundLiterals :+ groundLiteral
      val assignPairs: Seq[(Variable, Constant)] = literal.parameterVariables zip groundLiteral.parameter
      val updatedAssignMap: Map[Variable, Constant] = assignPairs.foldLeft(assignMap){ case (aMap,(variable, constant)) => aMap + (variable -> constant)}

      //Check if all preconditions of the task have been assigned
      if(updatedPrecons.isEmpty){
        //Check if all parameters of the task have been assigned
        if(task.parameters.size == updatedAssignMap.keys.size) {
          val arguments: Seq[Constant] = task.parameters map { variable => updatedAssignMap(variable)}
          Set(GroundTask(task, arguments))
        } else {
          val unassignedVariables: Seq[Variable] = task.parameters filterNot{ variable => updatedAssignMap.keySet contains variable}
          val variablesWithPossibleSubstitutions: Seq[(Variable, Seq[Constant])] = unassignedVariables map { variable => (variable, variable.sort.elements )}

          def computeSubstitutionCombinations[A](sets:Seq[Seq[A]]) : Seq[Seq[A]] = sets match { case Nil => List(Nil)
          case s+:ss => computeSubstitutionCombinations(ss).flatMap(s2 => s.map(_ +: s2)) }

          val possibleSubstitutionCombinations: Seq[Seq[Constant]] = computeSubstitutionCombinations(variablesWithPossibleSubstitutions map { case (v,c) => c})

          val possibleSubstitutionCombinationsWithVariables: Seq[Seq[(Variable, Constant)]] = possibleSubstitutionCombinations map { seq => unassignedVariables.zip(seq)}
          val allArgumentCombinations: Seq[Seq[Constant]] = possibleSubstitutionCombinationsWithVariables map { combination =>
                                                            task.parameters map { variable => updatedAssignMap.getOrElse(variable, combination.find{case (v,c) => v == variable}.get._2)}}
          (allArgumentCombinations map {arguments => GroundTask(task, arguments)}).toSet
        }
      } else {
        val nextLiteral = updatedPrecons.head

        def checkForMutexes(potentialGroundLiteral: GroundLiteral): Boolean = {
          val allGroundLiterals = updatedGroundLiterals :+ potentialGroundLiteral
          (for (x <- allGroundLiterals; y <- allGroundLiterals) yield (x, y)) exists { potentialMutex => !mutexes(potentialMutex) }
        }
        def checkCorrectAssignment(checkedLiteral: Literal, potentialGroundLiteral: GroundLiteral, assignmentMap: Map[Variable, Constant]): Boolean = {
          (checkedLiteral.parameterVariables zip potentialGroundLiteral.parameter) forall { case (variable, constant) => assignmentMap.getOrElse(variable, constant) == constant }
        }

        (preconMap(nextLiteral.predicate) filter { gLCandidate => checkForMutexes(gLCandidate) && checkCorrectAssignment(nextLiteral, gLCandidate, updatedAssignMap)} flatMap {
          nextGroundLiteral => createActionInstances(task, nextGroundLiteral, nextLiteral, updatedPrecons, mutexes, updatedAssignMap, updatedGroundLiterals)}).toSet
      }
    }

    def fillPreconMap(propositions: Set[GroundLiteral]): Unit = propositions.foreach(p => preconMap.addBinding(p.predicate, p))

    buildGraph((Set.empty[GroundTask], Set.empty[(GroundTask, GroundTask)], initialState, Set.empty[(GroundLiteral, GroundLiteral)]), initialState, Set.empty[(GroundLiteral, GroundLiteral)])
  }
  private val preconMap = new HashMap[Predicate, collection.mutable.Set[GroundLiteral]] with MultiMap[Predicate, GroundLiteral]

  private def createNOOP(groundLiteral: GroundLiteral): GroundTask = {
    val parameters: Seq[Variable] = groundLiteral.parameter map { constant => Variable(0, "no-op", (domain.sorts find { sort => sort.elements contains constant}).get)}
    val literal: Literal = Literal(groundLiteral.predicate, true, parameters)
    val task: ReducedTask = ReducedTask("NO-OP", true, parameters, Seq.empty[VariableConstraint], And(Vector(literal)), And(Vector(literal)))
    GroundTask(task, groundLiteral.parameter)
  }
}
