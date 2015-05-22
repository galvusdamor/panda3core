package de.uniulm.ki.panda3.domain

import de.uniulm.ki.panda3.csp.{CSP, SymbolicCSP}
import de.uniulm.ki.panda3.logic.Literal
import de.uniulm.ki.panda3.plan.element.{CausalLink, OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.plan.ordering.{SymbolicTaskOrdering, TaskOrdering}
import de.uniulm.ki.panda3.plan.{Plan, SymbolicPlan}

/**
 * The third example domain, the first to contain an abstract task and decomposition methods
 *
 * The domain contains (in addition to everything [[HasExampleDomain2]] contains) an abstract task, which has the same precondition -p1(?x1) and effect p1(?x1) as task1
 *
 * There are two decomposition methods, one containing no causal link and one which does
 *
 *
 *
 * TODO: There should be a second test abstract task for
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait HasExampleDomain3 extends HasExampleDomain2 {

  val abstractTask1: Task = Task("abstractTask_1", isPrimitive = false, variableSort1(7) :: Nil, Nil, preconditions = Literal(predicate1, isPositive = false, variableSort1(7) :: Nil) :: Nil,
                                 effects = Literal(predicate1, isPositive = true, variableSort1(7) :: Nil) :: Nil)


  // decomposition method 1 -- the one without causal links
  val initTaskOfPlanOfDecompositionMethod1: Task = Task("initM1", isPrimitive = true, variableSort1(8) :: Nil, Nil, preconditions = Nil,
                                                        effects = Literal(predicate1, isPositive = false, variableSort1(8) :: Nil) :: Nil)
  val goalTaskOfPlanOfDecompositionMethod1: Task = Task("goalM1", isPrimitive = true, variableSort1(9) :: Nil, Nil,
                                                        preconditions = Literal(predicate1, isPositive = true, variableSort1(9) :: Nil) :: Nil, effects = Nil)

  val initOfPlanOfDecompositionMethod1: PlanStep = PlanStep(0, initTaskOfPlanOfDecompositionMethod1, variableSort1(7) :: Nil)
  val goalOfPlanOfDecompositionMethod1: PlanStep = PlanStep(2, goalTaskOfPlanOfDecompositionMethod1, variableSort1(7) :: Nil)
  val actualPlanStepOfPlanOfDecompositionMethod1: PlanStep     = PlanStep(1, task1, variableSort1(7) :: Nil)
  val planStepsOfPlanOfDecompositionMethod1                    = initOfPlanOfDecompositionMethod1 :: goalOfPlanOfDecompositionMethod1 :: actualPlanStepOfPlanOfDecompositionMethod1 :: Nil
  val taskOrderingOfPlanOfDecompositionMethod1  : TaskOrdering = SymbolicTaskOrdering(OrderingConstraint(initOfPlanOfDecompositionMethod1, actualPlanStepOfPlanOfDecompositionMethod1)
                                                                                        :: OrderingConstraint(actualPlanStepOfPlanOfDecompositionMethod1,
                                                                                                              goalOfPlanOfDecompositionMethod1) :: Nil, planStepsOfPlanOfDecompositionMethod1)
  val cspOfPlanOfDecompositionMethod1           : CSP          = SymbolicCSP(Set(variableSort1(7)), Nil)

  val planOfDecompositionMethod1: Plan = SymbolicPlan(planStepsOfPlanOfDecompositionMethod1, Nil, taskOrderingOfPlanOfDecompositionMethod1, cspOfPlanOfDecompositionMethod1,
                                                      initOfPlanOfDecompositionMethod1, goalOfPlanOfDecompositionMethod1)
  /** a decomposition method without causal links */
  val decompositionMethod1: DecompositionMethod = DecompositionMethod(abstractTask1, planOfDecompositionMethod1)


  // decomposition method 2 -- the one with causal links
  val causalLinksOfDecompositionMethod2: Seq[CausalLink] = CausalLink(initOfPlanOfDecompositionMethod1, actualPlanStepOfPlanOfDecompositionMethod1,
                                                                      initOfPlanOfDecompositionMethod1.substitutedEffects(0)) ::
    CausalLink(actualPlanStepOfPlanOfDecompositionMethod1, goalOfPlanOfDecompositionMethod1, goalOfPlanOfDecompositionMethod1.substitutedPreconditions(0)) :: Nil

  val planOfDecompositionMethod2: Plan = SymbolicPlan(planStepsOfPlanOfDecompositionMethod1, causalLinksOfDecompositionMethod2, taskOrderingOfPlanOfDecompositionMethod1,
                                                      cspOfPlanOfDecompositionMethod1, initOfPlanOfDecompositionMethod1, goalOfPlanOfDecompositionMethod1)

  /** a decomposition method with causal links */
  val decompositionMethod2: DecompositionMethod = DecompositionMethod(abstractTask1, planOfDecompositionMethod2)


  val domain3: Domain = Domain(sort1 :: Nil, predicate1 :: Nil, abstractTask1 :: task1 :: init :: goal1 :: Nil, decompositionMethod1 :: decompositionMethod2 :: Nil, Nil)
}