package de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability

import de.uniulm.ki.panda3.symbolic.csp.VariableConstraint
import de.uniulm.ki.panda3.symbolic.domain.datastructures.LayeredGroundedPrimitiveReachabilityAnalysis
import de.uniulm.ki.panda3.symbolic.domain.{Domain, ReducedTask, Task}
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask

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
                   addedPropositions: Set[GroundLiteral], deletedMutexes: Set[(GroundLiteral, GroundLiteral)],firstLayer: Boolean, oldPreconMap: Map[Predicate, Set[GroundLiteral]]):
                   Seq[(Set[GroundTask], Set[(GroundTask, GroundTask)], Set[GroundLiteral], Set[(GroundLiteral, GroundLiteral)])] = {

      val updatedPrecondMap = fillPreconMap(oldPreconMap, addedPropositions)

      val changedPropositions: Set[GroundLiteral] = addedPropositions ++ (deletedMutexes flatMap { mutex => Set(mutex._1, mutex._2)})
      val tasksToBeConsidered: Set[(GroundLiteral, Seq[ReducedTask])] = changedPropositions map { groundLiteral => (groundLiteral, domain.consumersOf(groundLiteral.predicate))}

      val newGroundTasksFromPreconditions: Set[GroundTask] = tasksToBeConsidered flatMap { case (groundLiteral, tasks) => tasks flatMap { task => disallowedTasks match {
        case Right(forbiddenLiftedTasks) => if (forbiddenLiftedTasks contains task) {
          Set.empty[GroundTask]
        } else {
          createActionInstances(task, groundLiteral, (task.precondition.conjuncts find { literal => literal.predicate == groundLiteral.predicate }).get,
            task.precondition.conjuncts, previousLayer._4, updatedPrecondMap)
        }
        case Left(forbiddenGroundTasks) => createActionInstances(task, groundLiteral, (task.precondition.conjuncts find { literal => literal.predicate == groundLiteral.predicate }).get,
          task.precondition.conjuncts, previousLayer._4, updatedPrecondMap)
      } } }


      val newGroundTasksFromParameters: Set[GroundTask] = firstLayer match {
        case true  => createActionInstancesForTasksWithoutPreconditions(domain.tasks collect {
          case t: ReducedTask => t } filter { reducedTask => reducedTask.precondition.conjuncts.isEmpty})
        case false => Set.empty[GroundTask]
      }
      val newInstantiatedGroundTasks: Set[GroundTask] = newGroundTasksFromPreconditions ++ newGroundTasksFromParameters
      val newNoOps: Set[GroundTask] = addedPropositions map { groundLiteral => createNOOP(groundLiteral)}

      val allGroundTasks: Set[GroundTask] = previousLayer._1 ++ newInstantiatedGroundTasks ++ newNoOps
      val groundTaskPairs: Set[(GroundTask, GroundTask)] = for (x <- allGroundTasks; y <- allGroundTasks) yield (x, y)

      val taskMutexes: Set[(GroundTask, GroundTask)] =  computeMutexes match {
        case true => isSerial match {
          case true => groundTaskPairs filterNot { case (groundTask1, groundTask2) => groundTask1.task.name == "NO-OP" || groundTask2.task.name == "NO-OP" }
          case false => groundTaskPairs filter { case (groundTask1, groundTask2) => (groundTask1.substitutedDelEffects intersect
            (groundTask2.substitutedAddEffects union groundTask2.substitutedPreconditions)).nonEmpty ||
            (for (x <- groundTask1.substitutedPreconditions; y <- groundTask2.substitutedPreconditions) yield (x, y)).exists(previousLayer._4.contains)
          }
        }
        case false => Set.empty[(GroundTask, GroundTask)]
      }
      val newPropositions: Set[GroundLiteral] = (newInstantiatedGroundTasks flatMap { newGroundTask => newGroundTask.substitutedAddEffects }) -- previousLayer._3
      /*
       * TODO: Think about a better way to compute proposition-mutexes.
       */
      val allPropositions: Set[GroundLiteral] = newPropositions ++ previousLayer._3
      val propositionsAndTheirProducers: Map[GroundLiteral, Set[GroundTask]] = (allPropositions map { groundLiteral => (groundLiteral, allGroundTasks filter {
        groundTask => groundTask.substitutedAddEffects contains groundLiteral})}).toMap
      val propositionPairs: Set[(GroundLiteral, GroundLiteral)] = for (x <- allPropositions; y <- allPropositions) yield (x, y)
      val uniquePropositionPairs: Set[(GroundLiteral, GroundLiteral)] = propositionPairs filter { pair => propositionPairs contains pair.swap }

      val propositionMutexes: Set[(GroundLiteral, GroundLiteral)] = computeMutexes match {
        case true => uniquePropositionPairs filter { case (groundLiteral1, groundLiteral2) =>
          (for (x <- propositionsAndTheirProducers(groundLiteral1); y <- propositionsAndTheirProducers(groundLiteral2)) yield (x, y)) forall {
            case groundTaskPair => taskMutexes(groundTaskPair) || taskMutexes(groundTaskPair) } }
        case false => Set.empty[(GroundLiteral, GroundLiteral)]
      }

      val thisLayer = (allGroundTasks, taskMutexes, allPropositions, propositionMutexes)
      if (newPropositions.isEmpty && previousLayer._4.size == propositionMutexes.size) {
        Seq(thisLayer)
      } else {
        thisLayer +: buildGraph(thisLayer, newPropositions, previousLayer._4 diff propositionMutexes, false, updatedPrecondMap)
      }
    }

    def createNOOP(groundLiteral: GroundLiteral): GroundTask = {
      val parameters: Seq[Variable] = groundLiteral.parameter map { constant => Variable(0, "no-op", (domain.sorts find { sort => sort.elements contains constant}).get)}
      val literal: Literal = Literal(groundLiteral.predicate, true, parameters)
      val task: ReducedTask = ReducedTask("NO-OP", true, parameters, Seq.empty[VariableConstraint], And(Vector(literal)), And(Vector(literal)))
      GroundTask(task, groundLiteral.parameter)
    }

    def createActionInstancesForTasksWithoutPreconditions(tasks: Seq[ReducedTask]): Set[GroundTask] = {
      (tasks flatMap { task => computeAllPossibleSubstitutionCombinations(task.parameters) map { combination => GroundTask(task, combination)}}).toSet
    }

    def computeAllPossibleSubstitutionCombinations(variables: Seq[Variable]): Seq[Seq[Constant]] = {
      val variablesWithPossibleSubstitutions: Seq[(Variable, Seq[Constant])] = variables map { variable => (variable, variable.sort.elements )}
      def computeSubstitutionCombinations[A](sets:Seq[Seq[A]]) : Seq[Seq[A]] = sets match {
        case Nil => List(Nil)
        case s+:ss => computeSubstitutionCombinations(ss).flatMap(s2 => s.map(_ +: s2)) }

      computeSubstitutionCombinations(variablesWithPossibleSubstitutions map { case (v,c) => c})
    }

    def createActionInstances(task: ReducedTask, groundLiteral: GroundLiteral, literal: Literal, unassignedPrecons: Seq[Literal], mutexes: Set[(GroundLiteral, GroundLiteral)],
                              preconMap: Map[Predicate, Set[GroundLiteral]], assignMap: Map[Variable, Constant] = Map(), groundLiterals: Seq[GroundLiteral] = Seq.empty[GroundLiteral]):
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
          val newGroundTask = Set(GroundTask(task, arguments))
          disallowedTasks match {
            case Left(disallowedGroundTasks) => if( disallowedGroundTasks contains newGroundTask) {
                Set.empty[GroundTask]
              } else {
                newGroundTask
              }
            case Right(disallowedLiftedTasks) => newGroundTask
          }
        } else {
          val unassignedVariables: Seq[Variable] = task.parameters filterNot{ variable => updatedAssignMap.keySet contains variable}

          val possibleSubstitutionCombinations: Seq[Seq[Constant]] = computeAllPossibleSubstitutionCombinations(unassignedVariables)

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

        preconMap(nextLiteral.predicate) filter { gLCandidate => checkForMutexes(gLCandidate) && checkCorrectAssignment(nextLiteral, gLCandidate, updatedAssignMap)} flatMap {
          nextGroundLiteral => createActionInstances(task, nextGroundLiteral, nextLiteral, updatedPrecons, mutexes, preconMap, updatedAssignMap, updatedGroundLiterals)}
      }
    }

    def fillPreconMap(preconMap: Map[Predicate, Set[GroundLiteral]], propositions: Set[GroundLiteral]): Map[Predicate, Set[GroundLiteral]] = propositions.foldLeft(preconMap){
      case (pMap, groundLiteral) => pMap + (groundLiteral.predicate -> (pMap(groundLiteral.predicate) + groundLiteral))}

    buildGraph((Set.empty[GroundTask], Set.empty[(GroundTask, GroundTask)], initialState, Set.empty[(GroundLiteral, GroundLiteral)]), initialState, Set.empty[(GroundLiteral, GroundLiteral)], true, Map())
  }

}
