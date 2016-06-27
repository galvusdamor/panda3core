package de.uniulm.ki.panda3.symbolic.domain.datastructures.hierarchicalreachability

import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.domain.{SimpleDecompositionMethod, GroundedDecompositionMethod, Domain}
import de.uniulm.ki.panda3.symbolic.domain.datastructures.{GroundedReachabilityAnalysis, GroundedPrimitiveReachabilityAnalysis}
import de.uniulm.ki.panda3.symbolic.logic.GroundLiteral
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EverythingIsHiearchicallyReachable(domain: Domain, initialPlan: Plan) extends GroundedReachabilityAnalysis {
  // TODO only provide applicable tasks
  private val allgroundings: Seq[GroundTask] = domain.allGroundedPrimitiveTasks ++ domain.allGroundedAbstractTasks

  override val reachableGroundLiterals     : Seq[GroundLiteral] = (allgroundings flatMap { _.substitutedEffects.toSet }) ++ initialPlan.groundedInitialState
  private  val reachableGroundLiteralsAsSet: Set[GroundLiteral] = reachableGroundLiterals.toSet
  override val reachableGroundedTasks      : Seq[GroundTask]    = allgroundings filter { _.substitutedPreconditionsSet subsetOf reachableGroundLiteralsAsSet }

  override val additionalTaskNeededToGround   : Seq[GroundTask]                  = Nil
  override val reachableGroundMethods         : Seq[GroundedDecompositionMethod] = domain.decompositionMethods flatMap {
    case method@SimpleDecompositionMethod(abstractTask, _) => reachableGroundedTasks filter { _.task == abstractTask } flatMap method.groundWithAbstractTaskGrounding
    case _                                                 => noSupport(NONSIMPLEMETHOD)

  }
  override val additionalMethodsNeededToGround: Seq[GroundedDecompositionMethod] = Nil
}


case class EverythingIsHiearchicallyReachableBasedOnPrimitiveReachability(domain: Domain, initialPlan: Plan, primitiveReachabilityAnalysis: GroundedPrimitiveReachabilityAnalysis)
  extends GroundedReachabilityAnalysis {
  override val reachableGroundedTasks         : Seq[GroundTask]                  = primitiveReachabilityAnalysis.reachableGroundPrimitiveActions ++ domain.allGroundedAbstractTasks
  override val additionalTaskNeededToGround   : Seq[GroundTask]                  = Nil
  override val reachableGroundMethods         : Seq[GroundedDecompositionMethod] = domain.decompositionMethods flatMap {
    case method@SimpleDecompositionMethod(abstractTask, _) => reachableGroundedTasks filter { _.task == abstractTask } flatMap method.groundWithAbstractTaskGrounding
    case _                                                 => noSupport(NONSIMPLEMETHOD)

  }
  override val additionalMethodsNeededToGround: Seq[GroundedDecompositionMethod] = Nil
  override val reachableGroundLiterals        : Seq[GroundLiteral]               = (reachableGroundedTasks flatMap { _.substitutedEffects.toSet }) ++ initialPlan.groundedInitialState
}
