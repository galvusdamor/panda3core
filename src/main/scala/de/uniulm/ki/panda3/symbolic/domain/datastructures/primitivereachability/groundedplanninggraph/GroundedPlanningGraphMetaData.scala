package de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability.groundedplanninggraph

import de.uniulm.ki.panda3.symbolic.logic.{GroundLiteral, Predicate}
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask

/**
  * @author Kristof Mickeleit (kristof.mickeleit@uni-ulm.de)
  *
  * Metadata about a layer in a grounded planning graph.
  * @param addedPropositions Propositions that were added in the previous layer.
  * @param deletedPropositionMutexes Proposition mutexes that were deleted in the previous layer.
  * @param previousInterferenceMutexes Interference mutexes of the previous layer.
  * @param previousCompetingNeedsMutexes Competing needs mutexes of the previous layer.
  * @param firstLayer True if this is the first layer of the graph, otherwise false.
  * @param predicateMap Map mapping predicates to propositions that contain their corresponding predicate.
  * @param producerMap Map mapping propositions to all actions that have the corresponding proposition as an add-effect.
  */
case class GroundedPlanningGraphMetaData( addedPropositions: Set[GroundLiteral] = Set.empty[GroundLiteral],
                                          deletedPropositionMutexes: Set[(GroundLiteral, GroundLiteral)] = Set.empty[(GroundLiteral, GroundLiteral)],
                                          previousInterferenceMutexes: Set[(GroundTask, GroundTask)] = Set.empty[(GroundTask, GroundTask)],
                                          previousCompetingNeedsMutexes: Set[(GroundTask, GroundTask)] = Set.empty[(GroundTask, GroundTask)],
                                          firstLayer: Boolean = false,
                                          predicateMap: Map[Predicate, Set[GroundLiteral]] = Map.empty[Predicate, Set[GroundLiteral]],
                                          producerMap: Map[GroundLiteral, Set[GroundTask]] = Map.empty[GroundLiteral, Set[GroundTask]]) {

}
