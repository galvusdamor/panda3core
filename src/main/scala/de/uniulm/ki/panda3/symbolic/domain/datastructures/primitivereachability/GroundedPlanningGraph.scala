package de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability

import de.uniulm.ki.panda3.symbolic.csp._
import de.uniulm.ki.panda3.symbolic.domain.datastructures.LayeredGroundedPrimitiveReachabilityAnalysis
import de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability.DebuggingMode.DebuggingMode
import de.uniulm.ki.panda3.symbolic.domain.{Task, Domain, ReducedTask}
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask


/**
  * @author Kristof Mickeleit (kristof.mickeleit@uni-ulm.de)
  *
  *         Configuration for a GroundedPlanningGraph
  * @constructor Creates a new configuration with the given parameters.
  * @param computeMutexes         If True the GroundedPlanningGraph will compute task and proposition mutexes.
  * @param isSerial               If True the GroundedPlanningGraph will compute additional mutexes making actions mutex with each other.
  * @param forbiddenLiftedTasks   Set of lifted tasks the GroundedPlanningGraph is forbid to instantiate.
  * @param forbiddenGroundedTasks Set of grounded tasks the GroundedPlanningGraph is forbid to instantiate.
  * @param buckets                Determines if buckets will be used for mutex computation.
  * @param debuggingMode          Determines what will be printed during the computation of the graph.
  */
case class GroundedPlanningGraphConfiguration(computeMutexes: Boolean = true,
                                              isSerial: Boolean = false,
                                              forbiddenLiftedTasks: Set[Task] = Set.empty[Task],
                                              forbiddenGroundedTasks: Set[GroundTask] = Set.empty[GroundTask],
                                              buckets: Boolean = false,
                                              debuggingMode: DebuggingMode = DebuggingMode.Disabled) {
}

object DebuggingMode extends Enumeration {
  type DebuggingMode = Value
  val Disabled, Short, Medium, Long = Value
}


/**
  * @author Kristof Mickeleit (kristof.mickeleit@uni-ulm.de)
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  * @author Daniel Höller (daniel.hoeller@uni-ulm.de)
  *
  *         GroundedPlanningGraph implementation.
  * @constructor Creates a new planning graph for the given domain, initial state and configuration.
  * @param domain        Domain containing all important information for the graph.
  * @param initialState  Set of grounded literals representing the initial state.
  * @param configuration Configuration determining how the graph will be computed.
  */
case class GroundedPlanningGraph(domain: Domain, initialState: Set[GroundLiteral], configuration: GroundedPlanningGraphConfiguration) extends LayeredGroundedPrimitiveReachabilityAnalysis {

  initialState foreach { f => assert(f.isPositive) }

  // Number of layers of the planning graph.
  lazy val graphSize: Int = layerWithMutexes.size

  // Layers of the planning graph, represented by sets of actions and propositions.
  override lazy val layer: Seq[(Set[GroundTask], Set[GroundLiteral])] = {
    // compute the layers
    val computedLayer = layerWithMutexes map { case (groundTasks, groundTaskMutexes, groundLiterals, groundLiteralMutexes) =>
      (groundTasks filterNot {
        _.task.name.startsWith("NO-OP")
      }, groundLiterals)
    }
    // check assertions
    computedLayer foreach { case (_, b) => b foreach { gl => assert(gl.isPositive) } }
    computedLayer foreach { case (_, b) => assert(initialState forall b.contains) }

    // return the graph
    computedLayer
  }

  /**
    * @author Kristof Mickeleit (kristof.mickeleit@uni-ulm.de)
    *
    *         Metadata about a layer in a grounded planning graph.
    * @param addedPropositions             Propositions that were added in the previous layer.
    * @param deletedPropositionMutexes     Proposition mutexes that were deleted in the previous layer.
    * @param previousInterferenceMutexes   Interference mutexes of the previous layer.
    * @param previousCompetingNeedsMutexes Competing needs mutexes of the previous layer.
    * @param firstLayer                    True if this is the first layer of the graph, otherwise false.
    * @param predicateMap                  Map mapping predicates to propositions that contain their corresponding predicate.
    * @param preconditionBuckets           Map mapping propositions to all actions that have the corresponding proposition as a precondition.
    * @param addBuckets                    Map mapping propositions to all actions that have the corresponding proposition as an add-effect.
    * @param deleteBuckets                 Map mapping propositions to all actions that have the corresponding proposition as an delete-effect.
    */
  private case class GroundedPlanningGraphMetaData(addedPropositions: Set[GroundLiteral] = Set.empty[GroundLiteral],
                                                   deletedPropositionMutexes: Set[(GroundLiteral, GroundLiteral)] = Set.empty[(GroundLiteral, GroundLiteral)],
                                                   previousInterferenceMutexes: Set[(GroundTask, GroundTask)] = Set.empty[(GroundTask, GroundTask)],
                                                   previousCompetingNeedsMutexes: Set[(GroundTask, GroundTask)] = Set.empty[(GroundTask, GroundTask)],
                                                   firstLayer: Boolean = false,
                                                   predicateMap: Map[Predicate, Set[GroundLiteral]] = Map.empty[Predicate, Set[GroundLiteral]],
                                                   preconditionBuckets: Map[GroundLiteral, Set[GroundTask]] = Map.empty[GroundLiteral, Set[GroundTask]].withDefaultValue(Set()),
                                                   addBuckets: Map[GroundLiteral, Set[GroundTask]] = Map.empty[GroundLiteral, Set[GroundTask]].withDefaultValue(Set()),
                                                   deleteBuckets: Map[GroundLiteral, Set[GroundTask]] = Map.empty[GroundLiteral, Set[GroundTask]].withDefaultValue(Set())) {

  }


  // Layers of the planning graph represented by sets of actions and propositions and their mutexes.
  lazy val layerWithMutexes: Seq[(Set[GroundTask], Set[(GroundTask, GroundTask)], Set[GroundLiteral], Set[(GroundLiteral, GroundLiteral)])] = {
    val originalLayers = buildGraph(graph = Seq((Set.empty[GroundTask], Set.empty[(GroundTask, GroundTask)], initialState, Set.empty[(GroundLiteral, GroundLiteral)])),
      metaData = GroundedPlanningGraphMetaData(addedPropositions = initialState, firstLayer = true))

    originalLayers map {
      _._2
    } foreach {
      _ foreach { case (action1, action2) => assert(action1 != action2) }
    }
    originalLayers map {
      _._4
    } foreach {
      _ foreach { case (predicate1, predicate2) => assert(predicate1 != predicate2) }
    }

    originalLayers
  }

  /**
    * Function to compute the actual planning graph.
    *
    * @param graph    The current graph.
    * @param metaData Metadata about the last layer of the current graph.
    * @return Returns the completely computed graph given the domain, initial state and configuration.
    */
  private def buildGraph(graph: Seq[(Set[GroundTask], Set[(GroundTask, GroundTask)], Set[GroundLiteral], Set[(GroundLiteral, GroundLiteral)])], metaData: GroundedPlanningGraphMetaData):
  Seq[(Set[GroundTask], Set[(GroundTask, GroundTask)], Set[GroundLiteral], Set[(GroundLiteral, GroundLiteral)])] = {

    // Last computed layer in the current graph or if this is the first layer an empty layer with the initial state as propositions.
    val previousLayer = if (metaData.firstLayer) (Set.empty[GroundTask], Set.empty[(GroundTask, GroundTask)], initialState, Set.empty[(GroundLiteral, GroundLiteral)]) else graph.last

    val updatedPredicateMap = updatePredicateMap(metaData.predicateMap, metaData.addedPropositions)

    // New actions whose instantiation was made possible by added propositions and deleted proposition-mutexes of the previous layer.
    val newActions: Set[GroundTask] = computeNewActions(domain, configuration, metaData.firstLayer, metaData.addedPropositions,
      metaData.deletedPropositionMutexes, updatedPredicateMap, previousLayer._4)

    val (updatedPreBuckets, updatedAddBuckets, updatedDelBuckets) = updateBuckets(metaData.preconditionBuckets, metaData.addBuckets, metaData.deleteBuckets, newActions, configuration)
    val allActions: Set[GroundTask] = previousLayer._1 ++ newActions

    val (interferenceMutexes, competingNeedsMutexes) = configuration.computeMutexes match {
      case true => configuration.buckets match {
        case true => computeActionMutexesWBuckets(configuration, newActions, metaData.previousInterferenceMutexes, previousLayer._4, updatedPreBuckets, updatedAddBuckets, updatedDelBuckets)
        case false => computeActionMutexes(configuration, newActions, previousLayer._1, metaData.previousInterferenceMutexes, metaData.previousCompetingNeedsMutexes, previousLayer._4)
      }
      case false => (Set.empty[(GroundTask, GroundTask)], Set.empty[(GroundTask, GroundTask)])
    }
    val serialMutexes = configuration.isSerial match {
      case true => computeSerialMutexes(newActions, allActions)
      case false => Set.empty[(GroundTask, GroundTask)]
    }
    val allActionMutexes = serialMutexes ++ interferenceMutexes ++ competingNeedsMutexes

    // New propositions that were made available through the newly instantiated actions.
    val newPropositions: Set[GroundLiteral] = (newActions flatMap { action => action.substitutedAddEffects }) -- previousLayer._3

    val allPropositions: Set[GroundLiteral] = newPropositions ++ previousLayer._3

    val propositionMutexes: Set[(GroundLiteral, GroundLiteral)] = configuration.computeMutexes match {
      case true => computePropositionMutexes(configuration, newPropositions, previousLayer._3, allPropositions, allActionMutexes, updatedAddBuckets, previousLayer._4)
      case false => Set.empty[(GroundLiteral, GroundLiteral)]
    }

    val thisLayer = (allActions, allActionMutexes, allPropositions, propositionMutexes)

    /*
     * Determine if the computation of the planning graph should terminate.
     * First it needs to be checked if there are new propositions in this layer or the proposition mutexes changed.
     * If there are new propositions or proposition mutexes have changed the computation will continue.
     * If that is not the case it needs to be decided whether the current computed layer should be added to the graph or discarded.
     * New actions, changes in action mutexes or when the propositions of the last layer are equal to the initial state and there are no proposition mutexes
     * means that the current layer will be added to the graph, otherwise it will be discarded.
     */
    if (newPropositions.isEmpty && previousLayer._4.size == propositionMutexes.size) {
      if (previousLayer._1.size != allActions.size || previousLayer._2.size != allActionMutexes.size || (previousLayer._3 == initialState && previousLayer._4.isEmpty)) {
        printLayer(thisLayer, configuration.debuggingMode)
        graph :+ thisLayer
      } else {
        graph
      }
    } else {
      // Setting the new meta data about current graph.
      val newMetaData = GroundedPlanningGraphMetaData(addedPropositions = newPropositions,
        deletedPropositionMutexes = previousLayer._4 diff propositionMutexes,
        previousInterferenceMutexes = interferenceMutexes,
        previousCompetingNeedsMutexes = competingNeedsMutexes,
        predicateMap = updatedPredicateMap,
        preconditionBuckets = updatedPreBuckets,
        addBuckets = updatedAddBuckets,
        deleteBuckets = updatedDelBuckets
      )

      printLayer(thisLayer, configuration.debuggingMode)
      // Start the computation of the next layer of the graph.
      buildGraph(graph :+ thisLayer, newMetaData)
    }

  }

  /**
    * Prints various information of given graph layer. Amount of information depends on the debugging mode.
    *
    * TODO: Can probably be improved.
    *
    * @param thisLayer     Layer that information will be printed about.
    * @param debuggingMode Mode determining how much information will be printed.
    */
  def printLayer(thisLayer: (Set[GroundTask], Set[(GroundTask, GroundTask)], Set[GroundLiteral], Set[(GroundLiteral, GroundLiteral)]), debuggingMode: DebuggingMode): Unit = {
    if (debuggingMode.id > 0) {
      println("<======================>")
      println(s"Number of actions: ${thisLayer._1.size}")
      println(s"Number of action mutexes: ${thisLayer._2.size}")
      println(s"Number of propositions: ${thisLayer._3.size}")
      println(s"Number of proposition mutexes: ${thisLayer._4.size}")
      if (debuggingMode.id > 1) {
        println("=== Actions ===")
        for (x <- thisLayer._1) {
          println(x.task.name)
          if (debuggingMode.id > 2) {
            println("= Arguments =")
            for (y <- x.arguments) {
              println(y.name)
            }
          }
        }
        println("=== Action mutexes ===")
        for (x <- thisLayer._2) {
          println(s"[${x._1.task.name}, ${x._2.task.name}]")
          if (debuggingMode.id > 2) {
            println(x._1.task.name)
            println("= Arguments =")
            for (y <- x._1.arguments) {
              println(y.name)
            }
            println(x._2.task.name)
            println("= Arguments =")
            for (y <- x._2.arguments) {
              println(y.name)
            }
          }
        }
        println("=== Propositions ===")
        for (x <- thisLayer._3) {
          println(x.predicate.name)
          if (debuggingMode.id > 2) {
            for (y <- x.parameter) {
              println(y.name)
            }
          }
        }
        println("=== Proposition mutexes ===")
        for (x <- thisLayer._4) {
          println(s"[${x._1.predicate.name}, ${x._2.predicate.name}]")
          if (debuggingMode.id > 2) {
            println(x._1.predicate.name)
            println("= Constants =")
            for (y <- x._1.parameter) {
              println(y.name)
            }
            println(x._2.predicate.name)
            println("= Constants =")
            for (y <- x._2.parameter) {
              println(y.name)
            }
          }
        }
      }
      println("======================")
    }
  }

  /**
    * Compute the proposition mutexes.
    * Propositions are mutex if all their producers are mutex to each other.
    *
    * @param configuration         The configuration used for the graph.
    * @param newPropositions       New propositions added in the current layer.
    * @param oldPropositions       Old propositions from the previous layers.
    * @param allPropositions       All propositions available in the current layer.
    * @param actionMutexes         Action mutexes of the current layer.
    * @param producerMap           Map with propositions as keys and a sets of actions that have the proposition as an add-effect as corresponding values.
    * @param oldPropositionMutexes Proposition mutexes of the previous layer.
    * @return Returns a set of proposition pairs, representing the computed mutexes.
    */
  private def computePropositionMutexes(configuration: GroundedPlanningGraphConfiguration,
                                        newPropositions: Set[GroundLiteral],
                                        oldPropositions: Set[GroundLiteral],
                                        allPropositions: Set[GroundLiteral],
                                        actionMutexes: Set[(GroundTask, GroundTask)],
                                        producerMap: Map[GroundLiteral, Set[GroundTask]],
                                        oldPropositionMutexes: Set[(GroundLiteral, GroundLiteral)]): Set[(GroundLiteral, GroundLiteral)] = {
    val sortedNewPropositions: Vector[GroundLiteral] = newPropositions.toVector.sorted
    /*
      * Compute all possible pairs of propositions. First we compute the pairs containing two new propositions. This is accomplished by iteration with two generators.
      * Since the new propositions are sorted, the first generator only needs to iterate from 0 until the number of new propositions minus one.
      * The second generator only needs to iterate from the current value of the first generator until the number of new propositions.
      * The pairs containing at least one old proposition with the other being out of all propositions we iterate again with two generators.
      * The first iterating over old propositions, the second over all propositions. Since we cant order both sets in the same way, because they contain different elements, we need
      * to order each pair by its own.
      */
    val propositionPairs: Set[(GroundLiteral, GroundLiteral)] = (for (x <- 0 until sortedNewPropositions.size - 1; y <- x + 1 until sortedNewPropositions.size) yield
      (sortedNewPropositions(x), sortedNewPropositions(y))) ++
      (for (x <- oldPropositions; y <- sortedNewPropositions) yield if ((x compare y) < 0) (x, y) else (y, x)) ++ oldPropositionMutexes toSet

    // Compute all proposition mutexes by checking if all producers of each element of a pair are pairwise mutex or not.
    val propositionMutexes = propositionPairs filter { case (proposition1, proposition2) =>
      (for (x <- producerMap(proposition1); y <- producerMap(proposition2)) yield if ((x compare y) < 0) (x, y) else (y, x)) forall actionMutexes.contains
    }
    propositionMutexes
  }

  /**
    * Compute action mutexes with buckets.
    * There are two types of action mutexes:
    * - interference mutexes: Two actions are mutex if either of the actions deletes a precondition or Add-Effect of the other.
    * - competing needs mutexes: If there is a precondition of action a and a precondition of action b that
    * are marked as mutually exclusive of each other in the previous proposition level.
    *
    * @param configuration          The configuration used for the graph.
    * @param newActions             All actions instantiated in the current layer.
    * @param oldInterferenceMutexes Interference mutexes of the previous layer.
    * @param propositionMutexes     Proposition mutexes of the previous layer.
    * @param preconditionBuckets    Map mapping propositions to all actions that have the corresponding proposition as a precondition.
    * @param addBuckets             Map mapping propositions to all actions that have the corresponding proposition as an add-effect.
    * @param deleteBuckets          Map mapping propositions to all actions that have the corresponding proposition as an delete-effect.
    * @return Returns two sets, interference and competing needs mutexes represented as pairs of actions.
    */
  private def computeActionMutexesWBuckets(configuration: GroundedPlanningGraphConfiguration,
                                           newActions: Set[GroundTask],
                                           oldInterferenceMutexes: Set[(GroundTask, GroundTask)],
                                           propositionMutexes: Set[(GroundLiteral, GroundLiteral)],
                                           preconditionBuckets: Map[GroundLiteral, Set[GroundTask]],
                                           addBuckets: Map[GroundLiteral, Set[GroundTask]],
                                           deleteBuckets: Map[GroundLiteral, Set[GroundTask]]):
  (Set[(GroundTask, GroundTask)], Set[(GroundTask, GroundTask)]) = {
    // Get all propositions that where changed because of new actions.
    val (preconditions, addEffects, deleteEffects) =
      newActions.foldLeft((Set.empty[GroundLiteral], Set.empty[GroundLiteral], Set.empty[GroundLiteral]))(
        { case (tuple, action) =>
          (tuple._1 ++ action.substitutedPreconditions, tuple._2 ++ action.substitutedAddEffects, tuple._3 ++ (action.substitutedDelEffects map {
            _.copy(isPositive = true)
          }))
        })
    // Compute new interference mutexes and add the old ones afterwards.
    val interferenceMutexes: Set[(GroundTask, GroundTask)] = ((preconditions ++ addEffects ++ deleteEffects) flatMap { proposition =>
      for (x <- preconditionBuckets(proposition) ++ addBuckets(proposition); y <- deleteBuckets(proposition) if x != y)
        yield if ((x compare y) < 0) (x, y) else (y, x)
    }) ++ oldInterferenceMutexes
    // Compute competing needs mutexes.
    val competingNeedsMutexes: Set[(GroundTask, GroundTask)] = propositionMutexes flatMap { case (proposition1, proposition2) =>
      for (x <- preconditionBuckets(proposition1); y <- preconditionBuckets(proposition2) if x != y) yield if ((x compare y) < 0) (x, y) else (y, x)
    }
    // Return computed mutexes.
    (interferenceMutexes, competingNeedsMutexes)
  }

  /**
    * Compute action mutexes.
    * There are two types of action mutexes:
    * - interference mutexes: Two actions are mutex if either of the actions deletes a precondition or Add-Effect of the other.
    * - competing needs mutexes: If there is a precondition of action a and a precondition of action b that
    * are marked as mutually exclusive of each other in the previous proposition level.
    *
    * @param configuration            The configuration used for the graph.
    * @param newActions               All actions instantiated in the current layer.
    * @param oldActions               Actions that were instantiated in previous layers.
    * @param oldInterferenceMutexes   Interference mutexes of the previous layer.
    * @param oldCompetingNeedsMutexes Competing needs mutexes of the previous layer.
    * @param propositionMutexes       Proposition mutexes of the previous layer.
    * @return Returns two sets, interference and competing needs mutexes represented as pairs of actions.
    */
  private def computeActionMutexes(configuration: GroundedPlanningGraphConfiguration,
                                   newActions: Set[GroundTask],
                                   oldActions: Set[GroundTask],
                                   oldInterferenceMutexes: Set[(GroundTask, GroundTask)],
                                   oldCompetingNeedsMutexes: Set[(GroundTask, GroundTask)],
                                   propositionMutexes: Set[(GroundLiteral, GroundLiteral)]): (Set[(GroundTask, GroundTask)], Set[(GroundTask, GroundTask)]) = {
    val newActionsSorted = newActions.toVector.sorted
    val vectorSize = newActionsSorted.size
    /*
     * Use two generators to iterate over the vector of new actions to generate all action pairs containing only new actions.
     * Since the vector is sorted the first generator iterates from 0 until size of the vector minus one, the second vector needs only to iterate from
     * the current value of the first generator until the size of the vector to ensure all pairs are generated with minimal effort.
     */
    val newActionsPairs = for (x <- 0 until vectorSize - 1; y <- x + 1 until vectorSize) yield (newActionsSorted(x), newActionsSorted(y))
    /*
     * Use two generators to iterate over the sets of new and old actions. since the two sets cannot be converted to a vector and sorted in exactly the same order
     * like the new action pairs we need to order each pair by itself.
     */
    val oldNewActionPairs = for (x <- newActions; y <- oldActions if x != y) yield if ((x compare y) < 0) (x, y) else (y, x)
    /*
     * Search for interference mutexes in pairs containing either two new actions or one new and one old action. Afterwards add the old interference mutexes of the previous layer.
     * The old mutexes can be just added because interference mutexes never disappear.
     */
    val interferenceMutexes = ((newActionsPairs ++ oldNewActionPairs) filter { case (action1, action2) =>
      (action1.substitutedDelEffects exists { substitutedEffect => (action2.substitutedAddEffects ++ action2.substitutedPreconditions) exists {
        _.=!=(substitutedEffect)
      }
      }) ||
        (action2.substitutedDelEffects exists { substitutedEffect => (action1.substitutedAddEffects ++ action1.substitutedPreconditions) exists {
          _.=!=(substitutedEffect)
        }
        })
    }) ++ oldInterferenceMutexes
    /*
     * Search for competing needs mutexes in pairs containing two new actions or one new and one old action and
     * in the pairs contained in the competing needs mutexes of the previous layer. We need to order the precondition pairs again since proposition mutexes are ordered pairs too.
     */
    val competingNeedsMutexes = (newActionsPairs ++ oldNewActionPairs ++ oldCompetingNeedsMutexes) filter { case (action1, action2) =>
      (for (x <- action1.substitutedPreconditions; y <- action2.substitutedPreconditions) yield if ((x compare y) < 0) (x, y) else (y, x)).exists(propositionMutexes.contains)
    }
    (interferenceMutexes.toSet, competingNeedsMutexes.toSet)
  }

  /**
    * Compute serial action mutexes.
    *
    * @param newActions Actions that were instantiated in this layer.
    * @param allActions Old actions of the previous layer.
    * @return Returns a set containing mutex pairs.
    */
  private def computeSerialMutexes(newActions: Set[GroundTask], allActions: Set[GroundTask]): Set[(GroundTask, GroundTask)] = {
    // Filter NO-OP actions, since NO-OPs are not considered for serial mutexes.
    val filteredNewActions = (newActions filterNot { action => action.task.name.startsWith("NO-OP") }).toVector.sorted
    val filteredAllActions = allActions filterNot { action => action.task.name.startsWith("NO-OP") }
    ((for (x <- 0 until filteredNewActions.size - 1; y <- x + 1 until filteredNewActions.size) yield (filteredNewActions(x), filteredNewActions(y))) ++
      (for (x <- filteredNewActions; y <- filteredAllActions if x != y) yield if ((x compare y) < 0 && x != y) (x, y) else (y, x))) toSet
  }

  /**
    * Compute new actions that can be instantiated because new propositions became available or mutexes were deleted in the previous layer.
    *
    * @param domain             Domain containing all important information for the graph.
    * @param configuration      The configuration used for the graph.
    * @param firstLayer         True if the first layer of the graph is currently computed.
    * @param addedPropositions  Propositions that were added in the previous graph layer or in case it is the first layer the initial state.
    * @param deletedMutexes     Mutexes that were removed in the previous layer.
    * @param predicateMap       Map containing predicates as keys and sets of every available proposition with the corresponding predicate as values.
    * @param propositionMutexes Proposition mutexes for propositions of the previous layer.
    * @return Returns a set of newly instantiated actions.
    */
  private def computeNewActions(domain: Domain,
                                configuration: GroundedPlanningGraphConfiguration,
                                firstLayer: Boolean,
                                addedPropositions: Set[GroundLiteral],
                                deletedMutexes: Set[(GroundLiteral, GroundLiteral)],
                                predicateMap: Map[Predicate, Set[GroundLiteral]],
                                propositionMutexes: Set[(GroundLiteral, GroundLiteral)]): Set[GroundTask] = {

    /*
     * Find all tasks that could be up for instantiation because either at least one of their preconditions were added in the previous layer or
     * two of its preconditions were contained in a now deleted mutex. The tasks are paired up with propositions which are the reason the tasks are considered.
     */
    val tasksFromAddedPropositions: Set[(Set[GroundLiteral], Seq[ReducedTask])] = addedPropositions map { proposition => (Set(proposition), domain.consumersOf(proposition.predicate)) }
    val tasksFromDeletedMutexes: Set[(Set[GroundLiteral], Seq[ReducedTask])] =
      deletedMutexes map { case (proposition1, proposition2) =>
        (Set(proposition1, proposition2), domain.consumersOf(proposition1.predicate) intersect domain.consumersOf(proposition2.predicate))
      }

    // Merge the sets of tasks-proposition pairs that could be up for instantiation and filter them for forbidden lifted tasks, since they are not allowed to be instantiated.
    val tasksToConsider: Set[(Set[GroundLiteral], Seq[ReducedTask])] = (tasksFromAddedPropositions ++ tasksFromDeletedMutexes) map {
      case (propositions, tasks) => (propositions, tasks filterNot { task => configuration.forbiddenLiftedTasks contains task })
    }

    // Find for every tasks-proposition pair all preconditions of the tasks the propositions could be inserted for
    // to generate pairs consisting of a task and multiple proposition-precondition pairs.
    val tasksWithLiteralPairs = tasksToConsider flatMap { case (propositions, tasks) => tasks flatMap { task => findLiteralPairs(task, propositions) } }

    // Compute new actions based on the tasks with literal pairs.
    val newActions: Set[GroundTask] = tasksWithLiteralPairs flatMap { case (task, literalPairs) =>
      instantiateActions(task, literalPairs, configuration.forbiddenGroundedTasks, task.precondition.conjuncts, predicateMap, propositionMutexes)
    }

    // special treatment for tasks without preconditions. the are always applicable in the first action layer
    val newActionsFromParameters: Set[GroundTask] = firstLayer match {
      case true => domain.tasks filter { case task: ReducedTask =>
        task.precondition.isEmpty && !(configuration.forbiddenLiftedTasks contains task)
      } flatMap { case task: ReducedTask => createActionInstancesForTasksWithoutPreconditions(task) } toSet
      case false => Set.empty[GroundTask]
    }
    val newNoOps: Set[GroundTask] = addedPropositions map { proposition => createNOOP(proposition) }
    newActions ++ newActionsFromParameters ++ newNoOps
  }

  /**
    * Finds all combinations of valid proposition and lifted literal pairs given the task and propositions parameter.
    *
    * @param task           Task which preconditions will be used as literals for pairing.
    * @param groundLiterals Set of propositions that will be paired with the literals.
    * @return Returns a Set of tuple with a lifted task and a number of literal pairs.
    */
  private def findLiteralPairs(task: ReducedTask, groundLiterals: Set[GroundLiteral]): Seq[(ReducedTask, Seq[(GroundLiteral, Literal)])] = {
    val potentialLiterals: Set[(GroundLiteral, Set[Literal])] = groundLiterals map { proposition =>
      (proposition, Set(task.precondition.conjuncts filter { literal => literal.predicate == proposition.predicate }) flatten)
    }
    // TODO can code be removed?
    /*val literalPairs: Set[Seq[(GroundLiteral, Literal)]] =
      potentialLiterals.foldLeft[Set[Seq[(GroundLiteral, Literal)]]](Set(Nil))(
        { case (setOfPairs, (proposition, literals)) =>
          setOfPairs map { pairs: Seq[(GroundLiteral, Literal)] => pairs ++ (literals map { literal: Literal => (proposition, literal) })
          }
        })
    literalPairs map { setOfPairs => (task, setOfPairs) }*/

    potentialLiterals.foldLeft[Seq[(ReducedTask, Seq[(GroundLiteral, Literal)])]](Nil)(
      { case (old, (groundLiteral, preconditions)) => old ++ (preconditions map { prec => (task, (groundLiteral, prec) :: Nil) }) })
  }

  /**
    * Updates the predicate map with new propositions.
    *
    * @param predicateMap Map to be updated.
    * @param propositions Propositions to be added.
    * @return Returns the updated map.
    */
  private def updatePredicateMap(predicateMap: Map[Predicate, Set[GroundLiteral]], propositions: Set[GroundLiteral]): Map[Predicate, Set[GroundLiteral]] = {
    propositions.foldLeft(predicateMap) { case (pMap, proposition) =>
      pMap + (proposition.predicate -> (pMap.getOrElse(proposition.predicate, Set.empty[GroundLiteral]) + proposition))
    }
  }

  /**
    * Updates the buckets with new actions.
    * @param preconditionBuckets Bucket to be updated.
    * @param addBuckets Bucket to be updated.
    * @param deleteBuckets Bucket to be updated.
    * @param newActions Set of actions the buckets will be updated with.
    * @param configuration Configuration of the planning graph.
    * @return Returns the updated buckets.
    */
  private def updateBuckets(preconditionBuckets: Map[GroundLiteral, Set[GroundTask]],
                            addBuckets: Map[GroundLiteral, Set[GroundTask]],
                            deleteBuckets: Map[GroundLiteral, Set[GroundTask]],
                            newActions: Set[GroundTask],
                            configuration: GroundedPlanningGraphConfiguration):
  (Map[GroundLiteral, Set[GroundTask]], Map[GroundLiteral, Set[GroundTask]], Map[GroundLiteral, Set[GroundTask]]) = {
    val (updatedPreBucket, updatedAddBucket, updatedDelBucket) = newActions.foldLeft((preconditionBuckets, addBuckets, deleteBuckets)) { case(buckets, action) =>
        (configuration.buckets match {
          case true => action.substitutedPreconditions.foldLeft(buckets._1) { case(pBuckets, proposition) =>
          pBuckets + (proposition -> (pBuckets.getOrElse(proposition, Set.empty[GroundTask]) + action))}
          case false => Map.empty[GroundLiteral, Set[GroundTask]]},
          action.substitutedAddEffects.foldLeft(buckets._2) { case(aBuckets, proposition) =>
            aBuckets + (proposition -> (aBuckets.getOrElse(proposition, Set.empty[GroundTask]) + action))},
          configuration.buckets match {
            case true => action.substitutedDelEffects.foldLeft(buckets._3) { case(dBuckets, proposition) =>
            dBuckets + (proposition.copy(isPositive = true) -> (dBuckets.getOrElse(proposition.copy(isPositive = true), Set.empty[GroundTask]) + action))}
            case false => Map.empty[GroundLiteral, Set[GroundTask]]})
    }
    (updatedPreBucket, updatedAddBucket, updatedDelBucket)
  }


  /**
    * Instantiates new actions based on the given task with the literal pairs parameter.
    *
    * @param task                     Task the new actions will be based on.
    * @param literalPairs             Pairs of propositions and corresponding literals that are guaranteed to be used in the instantiation.
    * @param disallowedActions        Actions that are forbidden to instantiate.
    * @param unfulfilledPreconditions Preconditions that need to be fulfilled.
    * @param predicateMap             Map mapping predicates to propositions that contain their corresponding predicate.
    * @param mutexes                  Proposition mutexes of the previous layer.
    * @param assignmentMap            Map mapping variables to their assigned constants.
    * @param usedPropositions         All propositions that are already used in the process of action instantiation.
    * @return Returns a set of newly instantiated actions, all based on the given task.
    */
  private def instantiateActions(task: ReducedTask,
                                 literalPairs: Seq[(GroundLiteral, Literal)],
                                 disallowedActions: Set[GroundTask],
                                 unfulfilledPreconditions: Seq[Literal],
                                 predicateMap: Map[Predicate, Set[GroundLiteral]],
                                 mutexes: Set[(GroundLiteral, GroundLiteral)],
                                 assignmentMap: Map[Variable, Constant] = Map(),
                                 usedPropositions: Seq[GroundLiteral] = Seq.empty[GroundLiteral]): Set[GroundTask] = {

    if (literalPairs exists { case (proposition, literal) => proposition.parameter zip literal.parameterVariables exists { case (c, v) => !(v.sort.elements contains c) } }) {
      Set()
    } else {
      // TODO: could possibly removed
      assignmentMap foreach { case (v, c) => assert(v.sort.elements contains c) }
      val remainingLiteralPairs = literalPairs.tail
      val remainingUnfulfilledPreconditions = unfulfilledPreconditions filterNot (precondition => literalPairs.head._2 == precondition)
      val updatedUsedPropositions = usedPropositions :+ literalPairs.head._1
      val assignmentPairs: Seq[(Variable, Constant)] = literalPairs.head._2.parameterVariables zip literalPairs.head._1.parameter
      assignmentPairs foreach { case (v, c) => assert(v.sort.elements contains c) }
      val updatedAssignmentMap: Map[Variable, Constant] = assignmentPairs.foldLeft(assignmentMap) { case (aMap, (variable, constant)) => aMap + (variable -> constant) }

      // check whether we might have violated parameter constraints
      val taskConstraintsOK = task.parameterConstraints forall {
        case Equal(var1,
        var2: Variable) => if ((updatedAssignmentMap contains var1) && (updatedAssignmentMap contains var2)) updatedAssignmentMap(var1) == updatedAssignmentMap(var2)
        else true
        case Equal(var1, const: Constant) => if (updatedAssignmentMap contains var1) updatedAssignmentMap(var1) == const else true
        case NotEqual(var1,
        var2: Variable) => if ((updatedAssignmentMap contains var1) && (updatedAssignmentMap contains var2)) updatedAssignmentMap(var1) != updatedAssignmentMap(var2)
        else true
        case NotEqual(var1, const: Constant) => if (updatedAssignmentMap contains var1) updatedAssignmentMap(var1) != const else true
        case OfSort(vari, sort) => if (updatedAssignmentMap contains vari) sort.elements contains updatedAssignmentMap(vari) else true
        case NotOfSort(vari, sort) => if (updatedAssignmentMap contains vari) !(sort.elements contains updatedAssignmentMap(vari)) else true
      }


      if (taskConstraintsOK) {

        // If there is no more precondition to fulfill instantiation can begin.
        if (remainingUnfulfilledPreconditions.isEmpty) {

          // Check if any variables are unassigned.
          if (task.parameters.size == updatedAssignmentMap.keys.size) {
            val arguments: Seq[Constant] = task.parameters map { variable => updatedAssignmentMap(variable) }
            if (disallowedActions exists (action => action.task == task && action.arguments == arguments)) Set.empty else Set(GroundTask(task, arguments))
          } else {
            // If there are unassigned variables we need to find them.
            val unassignedVariables: Seq[Variable] = task.parameters filterNot { variable => updatedAssignmentMap.keySet contains variable }

            // Find all possible variable substitution to be able to instantiate all possible actions later.
            val possibleSubstitutionCombinations: Seq[Seq[(Variable,Constant)]] =
            unassignedVariables.foldLeft[Seq[Seq[(Variable, Constant)]]](Nil :: Nil)({ case (args, variable) => variable.sort.elements flatMap { c => args map {
                _ :+ (variable, c)
              }
              }
              })

            // Compute all argument combinations and initiate a new action for each of them.
            val allArgumentCombinations: Seq[Seq[Constant]] = possibleSubstitutionCombinations map { combination =>
              task.parameters map { variable => updatedAssignmentMap.getOrElse(variable, combination.find { case (v, c) => v == variable }.get._2) }
            }
            (allArgumentCombinations collect { case arguments if task areParametersAllowed arguments => GroundTask(task, arguments) }).toSet
          }
        } else {

          if (remainingLiteralPairs.isEmpty) {
            val nextLiteral = remainingUnfulfilledPreconditions.head
            val preconditionCandidates = predicateMap.getOrElse(nextLiteral.predicate, Set.empty[GroundLiteral]) filter {
              gLCandidate => isMutexFree(updatedUsedPropositions, mutexes, gLCandidate) && !checkForAssignmentConflict(nextLiteral, gLCandidate, updatedAssignmentMap)
            }

            preconditionCandidates flatMap {
              nextPrecondition => instantiateActions(task, Seq((nextPrecondition, nextLiteral)), disallowedActions,
                remainingUnfulfilledPreconditions, predicateMap, mutexes, updatedAssignmentMap, updatedUsedPropositions)
            }
          } else {
            instantiateActions(task, remainingLiteralPairs, disallowedActions, remainingUnfulfilledPreconditions, predicateMap, mutexes, updatedAssignmentMap, updatedUsedPropositions)
          }
        }
      } else Set()
    }
  }

  /**
    * Checks if the lifted and grounded literals are in conflict with the assignment map.
    *
    * @param checkedLiteral         Lifted literal whose variables will be used for checking.
    * @param potentialGroundLiteral Grounded literal whose constants will be used for checking.
    * @param assignmentMap          Map holding variable-constant assignments that will be checked for conflicts with the variable-constant pairs of the lifted and grounded literal.
    * @return Returns true if there is an assignment conflict, otherwise false.
    */
  private def checkForAssignmentConflict(checkedLiteral: Literal, potentialGroundLiteral: GroundLiteral, assignmentMap: Map[Variable, Constant]): Boolean = {
    (checkedLiteral.parameterVariables zip potentialGroundLiteral.parameter) exists { case (variable, constant) => assignmentMap.getOrElse(variable, constant) != constant }
  }

  /**
    * Checks for mutexes of given grounded literals.
    *
    * @param propositions         Sequence of propositions that could be part of a mutex pair.
    * @param mutexes              Set of propositions that are mutex.
    * @param potentialProposition Proposition that is potentially part of a mutex pair.
    * @return Returns true if the grounded literals are mutex free, otherwise false.
    */
  private def isMutexFree(propositions: Seq[GroundLiteral], mutexes: Set[(GroundLiteral, GroundLiteral)], potentialProposition: GroundLiteral): Boolean = {
    val propositionPairs = for (proposition <- propositions) yield if ((proposition compare potentialProposition) < 0) (proposition, potentialProposition)
    else (potentialProposition, proposition)
    propositionPairs forall { potentialMutex => !(mutexes contains potentialMutex) }
  }

  /**
    * Create actions based on lifted tasks that don't have any preconditions.
    *
    * @param task Task the actions will be based on.
    * @return Returns a set of newly instantiated actions based on the given task.
    */
  private def createActionInstancesForTasksWithoutPreconditions(task: ReducedTask): Set[GroundTask] = {
    // Find all legit constant combinations to instantiate every possible action.
    task.parameters.foldLeft[Seq[Seq[Constant]]](Nil :: Nil)({ case (args, variable) => variable.sort.elements flatMap { c => args map {
      _ :+ c
    }
    }
    }) map { arguments => GroundTask(task, arguments) } toSet
  }

  /**
    * Creates a NO-OP action for the proposition parameter.
    *
    * @param proposition Proposition the NO-OP action will be created for.
    * @return Returns the NO-OP action.
    */
  private def createNOOP(proposition: GroundLiteral): GroundTask = {
    // Create a sequence of variables corresponding to the constants contained in the proposition.
    val parameters: Seq[Variable] = proposition.parameter.zipWithIndex map { case (constant, id) => Variable(id, "no-op",
      (domain.sorts find { sort => sort.elements contains constant }).get)
    }
    // Create a literal corresponding to the proposition.
    val literal: Literal = Literal(proposition.predicate, isPositive = true, parameters)
    // Create a lifted task that has the literal as precondition and add-effect.
    val task: ReducedTask = ReducedTask("NO-OP[" + proposition.predicate.name + "]",
      isPrimitive = true, parameters, Nil, Seq.empty[VariableConstraint], And(Vector(literal)), And(Vector(literal)))
    // Instantiate an action based on the lifted task and return it.
    GroundTask(task, proposition.parameter)
  }

}
