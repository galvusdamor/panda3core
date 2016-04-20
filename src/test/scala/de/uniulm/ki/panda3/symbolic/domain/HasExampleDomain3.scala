package de.uniulm.ki.panda3.symbolic.domain

import de.uniulm.ki.panda3.symbolic.csp.{CSP, SymbolicCSP}
import de.uniulm.ki.panda3.symbolic.logic.{And, Literal}
import de.uniulm.ki.panda3.symbolic.plan.element.{CausalLink, OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.ordering.{SymbolicTaskOrdering, TaskOrdering}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.search.{AllFlaws, AllModifications}

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
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off magic.number
trait HasExampleDomain3 extends HasExampleDomain2 {

  val abstractTask1: ReducedTask = ReducedTask("abstractTask_1", isPrimitive = false, variableSort1(7) :: Nil, Nil,
                                               precondition = And[Literal](Literal(predicate1, isPositive = false, variableSort1(7)
                                                 :: Nil) :: Nil), effect = And[Literal](Literal(predicate1, isPositive = true, variableSort1(7) :: Nil) :: Nil))


  // decomposition method 1 -- the one without causal links
  val initTaskOfPlanOfDecompositionMethod1: ReducedTask = ReducedTask("initM1", isPrimitive = true, variableSort1(8) :: Nil, Nil, precondition = And[Literal](Nil),
                                                                      effect = And[Literal](Literal(predicate1, isPositive = false, variableSort1(8) :: Nil) :: Nil))
  val goalTaskOfPlanOfDecompositionMethod1: ReducedTask = ReducedTask("goalM1", isPrimitive = true, variableSort1(9) :: Nil, Nil,
                                                                      precondition = And[Literal](Literal(predicate1, isPositive = true, variableSort1(9) :: Nil) :: Nil),
                                                                      effect = And[Literal](Nil))

  val initOfPlanOfDecompositionMethod1          : PlanStep     = PlanStep(0, initTaskOfPlanOfDecompositionMethod1, variableSort1(7) :: Nil)
  val goalOfPlanOfDecompositionMethod1          : PlanStep     = PlanStep(2, goalTaskOfPlanOfDecompositionMethod1, variableSort1(7) :: Nil)
  val actualPlanStepOfPlanOfDecompositionMethod1: PlanStep     = PlanStep(1, task1, variableSort1(7) :: Nil)
  val planStepsOfPlanOfDecompositionMethod1                    = initOfPlanOfDecompositionMethod1 :: goalOfPlanOfDecompositionMethod1 :: actualPlanStepOfPlanOfDecompositionMethod1 :: Nil
  val taskOrderingOfPlanOfDecompositionMethod1  : TaskOrdering = SymbolicTaskOrdering(OrderingConstraint(initOfPlanOfDecompositionMethod1, actualPlanStepOfPlanOfDecompositionMethod1)
                                                                                        :: OrderingConstraint(actualPlanStepOfPlanOfDecompositionMethod1,
                                                                                                              goalOfPlanOfDecompositionMethod1) :: Nil, planStepsOfPlanOfDecompositionMethod1)
  val cspOfPlanOfDecompositionMethod1           : CSP          = SymbolicCSP(Set(variableSort1(7)), Nil)

  val planOfDecompositionMethod1: Plan                      = Plan(planStepsOfPlanOfDecompositionMethod1, Nil, taskOrderingOfPlanOfDecompositionMethod1,
                                                                           cspOfPlanOfDecompositionMethod1, initOfPlanOfDecompositionMethod1, goalOfPlanOfDecompositionMethod1,
                                                                           AllModifications, AllFlaws, Map(), Map())
  /** a decomposition method without causal links */
  val decompositionMethod1      : SimpleDecompositionMethod = SimpleDecompositionMethod(abstractTask1, planOfDecompositionMethod1)


  // decomposition method 2 -- the one with causal links
  val causalLinksOfDecompositionMethod2: Seq[CausalLink] = CausalLink(initOfPlanOfDecompositionMethod1, actualPlanStepOfPlanOfDecompositionMethod1,
                                                                      initOfPlanOfDecompositionMethod1.substitutedEffects.head) ::
    CausalLink(actualPlanStepOfPlanOfDecompositionMethod1, goalOfPlanOfDecompositionMethod1, goalOfPlanOfDecompositionMethod1.substitutedPreconditions.head) :: Nil

  val planOfDecompositionMethod2: Plan = Plan(planStepsOfPlanOfDecompositionMethod1, causalLinksOfDecompositionMethod2, taskOrderingOfPlanOfDecompositionMethod1,
                                                      cspOfPlanOfDecompositionMethod1, initOfPlanOfDecompositionMethod1, goalOfPlanOfDecompositionMethod1, AllModifications, AllFlaws, Map(),
                                                      Map())

  /** a decomposition method with causal links */
  val decompositionMethod2: SimpleDecompositionMethod = SimpleDecompositionMethod(abstractTask1, planOfDecompositionMethod2)


  val domain3: Domain = Domain(sort1 :: Nil, predicate1 :: Nil, abstractTask1 :: task1 :: init :: goal1 :: Nil, decompositionMethod1 :: decompositionMethod2 :: Nil, Nil)
}