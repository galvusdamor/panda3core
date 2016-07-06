package de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability

import de.uniulm.ki.panda3.symbolic.csp._
import de.uniulm.ki.panda3.symbolic.domain.datastructures.LayeredGroundedPrimitiveReachabilityAnalysis
import de.uniulm.ki.panda3.symbolic.domain.{Domain, ReducedTask, Task}
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask

/**
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class GroundedPlanningGraph
(domain: Domain, initialState: Set[GroundLiteral], computeMutexes: Boolean, isSerial: Boolean, disallowedTasks: Either[Seq[GroundTask], Seq[Task]] = Left(Nil))
  extends LayeredGroundedPrimitiveReachabilityAnalysis {

  initialState foreach { f => assert(f.isPositive)}

  lazy val graphSize: Int = layerWithMutexes.size
  // This function should compute the actual planning graph
  override lazy val layer: Seq[(Set[GroundTask], Set[GroundLiteral])] = layerWithMutexes map { case (groundTasks, groundTaskMutexes, groundLiterals, groundLiteralMutexes) =>
    (groundTasks filterNot {
      _.task.name.startsWith("NO-OP")
    }, groundLiterals)
  }

  layer foreach { case (_, b) => b foreach { gl => assert(gl.isPositive)}}

  lazy val layerWithMutexes: Seq[(Set[GroundTask], Set[(GroundTask, GroundTask)], Set[GroundLiteral], Set[(GroundLiteral, GroundLiteral)])] = {

    def buildGraph(previousLayer: (Set[GroundTask], Set[(GroundTask, GroundTask)], Set[GroundLiteral], Set[(GroundLiteral, GroundLiteral)]),
                   addedPropositions: Set[GroundLiteral], deletedMutexes: Set[(GroundLiteral, GroundLiteral)], firstLayer: Boolean, oldPreconMap: Map[Predicate, Set[GroundLiteral]]):
    Seq[(Set[GroundTask], Set[(GroundTask, GroundTask)], Set[GroundLiteral], Set[(GroundLiteral, GroundLiteral)])] = {

      val updatedPrecondMap = fillPreconMap(oldPreconMap, addedPropositions)

      // determine which grounded literals in the last state layer may cause new grounded actions to be applicable
      val changedPropositions: Set[GroundLiteral] = addedPropositions ++ (deletedMutexes flatMap { mutex => Set(mutex._1, mutex._2)})
      val tasksToBeConsidered: Set[(GroundLiteral, Seq[ReducedTask])] = changedPropositions map { groundLiteral => (groundLiteral, domain.consumersOf(groundLiteral.predicate))}


      // create the newly applicable grounded actions
      val newGroundTasksFromPreconditions: Set[GroundTask] = tasksToBeConsidered flatMap { case (groundLiteral, tasks) => tasks flatMap { task => disallowedTasks match {
        case Right(forbiddenLiftedTasks) if forbiddenLiftedTasks contains task => Set.empty[GroundTask]
        case _ =>
          task.precondition.conjuncts filter { literal => literal.predicate == groundLiteral.predicate} flatMap { startLiteral =>
            createActionInstances(task, groundLiteral, startLiteral, task.precondition.conjuncts, previousLayer._4, updatedPrecondMap)
          }
      }
      }
      }
      // special treatment for tasks without preconditions. the are always applicable in the first action layer
      val newGroundTasksFromParameters: Set[GroundTask] = firstLayer match {
        case true => createActionInstancesForTasksWithoutPreconditions(domain.tasks collect {
          case t: ReducedTask => t
        } filter { reducedTask => reducedTask.precondition.conjuncts.isEmpty})
        case false => Set.empty[GroundTask]
      }
      // round up
      val newInstantiatedGroundTasks: Set[GroundTask] = newGroundTasksFromPreconditions ++ newGroundTasksFromParameters
      val newNoOps: Set[GroundTask] = addedPropositions map { groundLiteral => createNOOP(groundLiteral)}
      val allGroundTasks: Set[GroundTask] = previousLayer._1 ++ newInstantiatedGroundTasks ++ newNoOps
      println("Tasks " + allGroundTasks.size)


      // compute task mutexes anew
      // TODO here we could to things a lot more efficiently
      val groundTaskPairs: Set[(GroundTask, GroundTask)] = for (x <- allGroundTasks; y <- allGroundTasks) yield (x, y)
      val taskMutexes: Set[(GroundTask, GroundTask)] = {
        if (computeMutexes) {
          val normalMutexes = groundTaskPairs filter { case (groundTask1, groundTask2) =>
            (groundTask1.substitutedDelEffects exists { substitutedEffect =>
              (groundTask2.substitutedAddEffects ++ groundTask2.substitutedPreconditions) exists {
                _.=!=(substitutedEffect)
              }
            }) ||
              (for (x <- groundTask1.substitutedPreconditions; y <- groundTask2.substitutedPreconditions) yield (x, y)).exists(previousLayer._4.contains)
          }
          if (isSerial) {
            (groundTaskPairs filterNot { case (groundTask1, groundTask2) => groundTask1.task.name.startsWith("NO-OP") || groundTask2.task.name.startsWith("NO-OP")}) union normalMutexes
          } else {
            normalMutexes
          }
        } else {
          Set.empty[(GroundTask, GroundTask)]
        }
      } filter { case (groundTask1, groundTask2) => groundTask1 != groundTask2}
      println("Mutexes " + taskMutexes.size)


      // determine new propositions
      val newPropositions: Set[GroundLiteral] = (newInstantiatedGroundTasks flatMap { newGroundTask => newGroundTask.substitutedAddEffects}) -- previousLayer._3
      val allPropositions: Set[GroundLiteral] = newPropositions ++ previousLayer._3
      println("Literals " + allPropositions.size)

      // compute proposition mutexes anew
      // TODO here we could to things a lot more efficiently
      val propositionsAndTheirProducers: Map[GroundLiteral, Set[GroundTask]] = (allPropositions map { groundLiteral => (groundLiteral, allGroundTasks filter {
        groundTask => groundTask.substitutedAddEffects contains groundLiteral
      })
      }).toMap
      val propositionPairs: Set[(GroundLiteral, GroundLiteral)] = for (x <- allPropositions; y <- allPropositions) yield (x, y)
      val uniquePropositionPairs: Set[(GroundLiteral, GroundLiteral)] = propositionPairs filterNot { pair => pair._1 == pair._2}
      val propositionMutexes: Set[(GroundLiteral, GroundLiteral)] = computeMutexes match {
        case true => uniquePropositionPairs filter { case (groundLiteral1, groundLiteral2) =>
          (for (x <- propositionsAndTheirProducers(groundLiteral1); y <- propositionsAndTheirProducers(groundLiteral2)) yield (x, y)) forall {
            case groundTaskPair => (taskMutexes contains groundTaskPair) || (taskMutexes contains groundTaskPair.swap)
          }
        }
        case false => Set.empty[(GroundLiteral, GroundLiteral)]
      }
      println("Mutexes " + propositionMutexes.size)

      // termination and looping checks. Loop if something has changed compared with the previous layer of the PG
      val thisLayer = (allGroundTasks, taskMutexes, allPropositions, propositionMutexes)
      if (newPropositions.isEmpty && previousLayer._4.size == propositionMutexes.size) {
        if (previousLayer._1.size != allGroundTasks.size || previousLayer._2.size != taskMutexes.size || (previousLayer._3 == initialState && previousLayer._4.isEmpty))
          thisLayer :: Nil
        else
          Seq.empty[(Set[GroundTask], Set[(GroundTask, GroundTask)], Set[GroundLiteral], Set[(GroundLiteral, GroundLiteral)])]
      } else {
        thisLayer +: buildGraph(thisLayer, newPropositions, previousLayer._4 diff propositionMutexes, firstLayer = false, updatedPrecondMap)
      }
    }

    def createNOOP(groundLiteral: GroundLiteral): GroundTask = {
      val parameters: Seq[Variable] = groundLiteral.parameter.zipWithIndex map { case (constant, id) =>
        Variable(id, "no-op", (domain.sorts find { sort => sort.elements contains constant}).get)
      }
      val literal: Literal = Literal(groundLiteral.predicate, isPositive = true, parameters)
      val task: ReducedTask = ReducedTask("NO-OP[" + groundLiteral.predicate.name + "]", isPrimitive = true, parameters,
        Seq.empty[VariableConstraint], And(literal :: Nil), And(literal :: Nil))
      GroundTask(task, groundLiteral.parameter)
    }

    def createActionInstancesForTasksWithoutPreconditions(tasks: Seq[ReducedTask]): Set[GroundTask] = {
      (tasks flatMap { task => computeAllPossibleSubstitutionCombinations(task.parameters) map { combination => GroundTask(task, combination)}}).toSet
    }

    def computeAllPossibleSubstitutionCombinations(variables: Seq[Variable]): Seq[Seq[Constant]] = {
      val variablesWithPossibleSubstitutions: Seq[(Variable, Seq[Constant])] = variables map { variable => (variable, variable.sort.elements)}
      def computeSubstitutionCombinations[A](sets: Seq[Seq[A]]): Seq[Seq[A]] = sets match {
        case Nil => List(Nil)
        case s +: ss => computeSubstitutionCombinations(ss).flatMap(s2 => s.map(_ +: s2))
      }

      computeSubstitutionCombinations(variablesWithPossibleSubstitutions map { case (v, c) => c})
    }

    /**
     * Does the actual instantiation magic, iterals over pairs of preconditions (the current one is the argument literal) and a matching ground literal (currently groundLiteral).
     * Keeps the current assignment of arguments in the assignMap
     */
    def createActionInstances(task: ReducedTask, groundLiteral: GroundLiteral, literal: Literal, unassignedPrecons: Seq[Literal], mutexes: Set[(GroundLiteral, GroundLiteral)],
                              preconMap: Map[Predicate, Set[GroundLiteral]], assignMap: Map[Variable, Constant] = Map(), groundLiterals: Seq[GroundLiteral] = Seq.empty[GroundLiteral]):
    Set[GroundTask] =
    // TODO is that correct ????
      if (groundLiteral.parameter zip literal.parameterVariables exists { case (c, v) => !(v.sort.elements contains c)}) {
        Set()
      }
      else {
        assignMap foreach { case (v, c) => assert(v.sort.elements contains c)}

        val updatedPrecons: Seq[Literal] = unassignedPrecons filterNot {
          _ == literal
        }
        val updatedGroundLiterals = groundLiterals :+ groundLiteral
        val assignPairs: Seq[(Variable, Constant)] = literal.parameterVariables zip groundLiteral.parameter
        assignPairs foreach { case (v, c) => assert(v.sort.elements contains c)}
        val updatedAssignMap: Map[Variable, Constant] = assignPairs.foldLeft(assignMap) { case (aMap, (variable, constant)) => aMap + (variable -> constant)}

        // check whether we might have violated parameter constraints
        val taskConstraintsOK = task.parameterConstraints forall {
          case Equal(var1, var2: Variable) => if ((updatedAssignMap contains var1) && (updatedAssignMap contains var2)) updatedAssignMap(var1) == updatedAssignMap(var2) else true
          case Equal(var1, const: Constant) => if (updatedAssignMap contains var1) updatedAssignMap(var1) == const else true
          case NotEqual(var1, var2: Variable) => if ((updatedAssignMap contains var1) && (updatedAssignMap contains var2)) updatedAssignMap(var1) != updatedAssignMap(var2) else true
          case NotEqual(var1, const: Constant) => if (updatedAssignMap contains var1) updatedAssignMap(var1) != const else true
          case OfSort(vari, sort) => if (updatedAssignMap contains vari) sort.elements contains updatedAssignMap(vari) else true
          case NotOfSort(vari, sort) => if (updatedAssignMap contains vari) !(sort.elements contains updatedAssignMap(vari)) else true
        }
        if (taskConstraintsOK) {

          //Check if all preconditions of the task have been assigned
          if (updatedPrecons.isEmpty) {

            //Check if all parameters of the task have been assigned
            if (task.parameters.size == updatedAssignMap.keys.size) {
              val arguments: Seq[Constant] = task.parameters map { variable => updatedAssignMap(variable)}
              val newGroundTask = GroundTask(task, arguments)
              disallowedTasks match {
                case Left(disallowedGroundTasks) => if (disallowedGroundTasks contains newGroundTask) Set.empty[GroundTask] else Set(newGroundTask)
                case Right(disallowedLiftedTasks) => Set(newGroundTask)
              }
            } else {
              val unassignedVariables: Seq[Variable] = task.parameters filterNot { variable => updatedAssignMap.keySet contains variable}

              val possibleSubstitutionCombinations: Seq[Seq[Constant]] = computeAllPossibleSubstitutionCombinations(unassignedVariables)

              val possibleSubstitutionCombinationsWithVariables: Seq[Seq[(Variable, Constant)]] = possibleSubstitutionCombinations map { seq => unassignedVariables.zip(seq)}
              val allArgumentCombinations: Seq[Seq[Constant]] = possibleSubstitutionCombinationsWithVariables map { combination =>
                task.parameters map { variable => updatedAssignMap.getOrElse(variable, combination.find { case (v, c) => v == variable}.get._2)}
              }
              (allArgumentCombinations map { arguments => GroundTask(task, arguments)}).toSet
            }
          } else {
            val nextLiteral = updatedPrecons.head

            def isMutexFree(potentialGroundLiteral: GroundLiteral): Boolean = {
              val allGroundLiterals = updatedGroundLiterals :+ potentialGroundLiteral
              val groundLiteralPairs = for (x <- allGroundLiterals; y <- allGroundLiterals) yield (x, y)
              groundLiteralPairs forall { potentialMutex => !(mutexes contains potentialMutex)}
            }

            def checkCorrectAssignment(checkedLiteral: Literal, potentialGroundLiteral: GroundLiteral, assignmentMap: Map[Variable, Constant]): Boolean = {
              (checkedLiteral.parameterVariables zip potentialGroundLiteral.parameter) forall { case (variable, constant) => assignmentMap.getOrElse(variable, constant) == constant}
            }

            val possiblePrecs = preconMap.getOrElse(nextLiteral.predicate, Set.empty[GroundLiteral]) filter { gLCandidate => isMutexFree(gLCandidate) &&
              checkCorrectAssignment(nextLiteral, gLCandidate, updatedAssignMap)
            }

            possiblePrecs flatMap {
              nextGroundLiteral => createActionInstances(task, nextGroundLiteral, nextLiteral, updatedPrecons, mutexes, preconMap, updatedAssignMap, updatedGroundLiterals)
            }
          }
        } else Set()
      }

    def fillPreconMap(preconMap: Map[Predicate, Set[GroundLiteral]], propositions: Set[GroundLiteral]): Map[Predicate, Set[GroundLiteral]] =
      propositions
        .foldLeft(preconMap) { case (pMap, groundLiteral) => pMap + (groundLiteral.predicate -> (pMap.getOrElse(groundLiteral.predicate, Set.empty[GroundLiteral]) + groundLiteral))}

    // actual computation of the planning graph
    buildGraph((Set.empty[GroundTask], Set.empty[(GroundTask, GroundTask)], initialState, Set.empty[(GroundLiteral, GroundLiteral)]),
      initialState, Set.empty[(GroundLiteral, GroundLiteral)], firstLayer = true, Map())
  }

}

object GroundedPlanningGraph {
  def apply(domain: Domain, initialState: Seq[GroundLiteral], computeMutexes: Boolean, isSerial: Boolean): GroundedPlanningGraph =
    GroundedPlanningGraph(domain, initialState toSet, computeMutexes, isSerial, Left(Nil))

}
