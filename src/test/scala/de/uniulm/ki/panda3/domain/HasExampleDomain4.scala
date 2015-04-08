package de.uniulm.ki.panda3.domain

import de.uniulm.ki.panda3.csp.{CSP, SymbolicCSP}
import de.uniulm.ki.panda3.logic.Literal
import de.uniulm.ki.panda3.plan.element.{OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.plan.ordering.{SymbolicTaskOrdering, TaskOrdering}
import de.uniulm.ki.panda3.plan.{Plan, SymbolicPlan}

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait HasExampleDomain4 extends HasExampleDomain1 {

  val task3: Task = Task("task3", isPrimitive = true, variableSort1(2) :: Nil, Nil, preconditions = Literal(predicate1, isPositive = true, variableSort1(2) :: Nil) :: Nil, effects = Nil)

  val abstractTask2: Task = Task("abstractTask_2", isPrimitive = false, variableSort1(7) :: Nil, Nil, preconditions = Literal(predicate1, isPositive = true, variableSort1(7) :: Nil) :: Nil,
                                 effects = Nil)


  // decomposition method 3, with two tasks
  val initTaskOfPlanOfDecompositionMethod3: Task = Task("initM3", isPrimitive = true, variableSort1(8) :: Nil, Nil, preconditions = Nil,
                                                        effects = Literal(predicate1, isPositive = true, variableSort1(8) :: Nil) :: Nil)
  val goalTaskOfPlanOfDecompositionMethod3: Task = Task("goalM3", isPrimitive = true, Nil, Nil, preconditions = Nil, effects = Nil)

  val initOfPlanOfDecompositionMethod3           : PlanStep     = PlanStep(0, initTaskOfPlanOfDecompositionMethod3, variableSort1(7) :: Nil)
  val goalOfPlanOfDecompositionMethod3           : PlanStep     = PlanStep(3, goalTaskOfPlanOfDecompositionMethod3, Nil)
  val actualPlanStep1OfPlanOfDecompositionMethod3: PlanStep     = PlanStep(1, task1, variableSort1(8) :: Nil)
  val actualPlanStep2OfPlanOfDecompositionMethod3: PlanStep     = PlanStep(2, task1, variableSort1(9) :: Nil)
  val planStepsOfPlanOfDecompositionMethod3                     = initOfPlanOfDecompositionMethod3 :: goalOfPlanOfDecompositionMethod3 :: actualPlanStep1OfPlanOfDecompositionMethod3 ::
    actualPlanStep2OfPlanOfDecompositionMethod3 :: Nil
  val taskOrderingOfPlanOfDecompositionMethod3   : TaskOrdering = SymbolicTaskOrdering(OrderingConstraint.allBetween(initOfPlanOfDecompositionMethod3, goalOfPlanOfDecompositionMethod3,
                                                                                                                     actualPlanStep1OfPlanOfDecompositionMethod3,
                                                                                                                     actualPlanStep2OfPlanOfDecompositionMethod3),
                                                                                       planStepsOfPlanOfDecompositionMethod3)
  val cspOfPlanOfDecompositionMethod3            : CSP          = SymbolicCSP(Set(variableSort1(7)), Nil)

  val planOfDecompositionMethod3: Plan                = SymbolicPlan(planStepsOfPlanOfDecompositionMethod3, Nil, taskOrderingOfPlanOfDecompositionMethod3, cspOfPlanOfDecompositionMethod3,
                                                                     initOfPlanOfDecompositionMethod3, goalOfPlanOfDecompositionMethod3)
  /** a decomposition method without causal links */
  val decompositionMethod3      : DecompositionMethod = DecompositionMethod(abstractTask2, planOfDecompositionMethod3)

  val init4: Task = Task("init", isPrimitive = true, variableSort1(3) :: Nil, Nil, preconditions = Nil, effects = Literal(predicate1, isPositive = true, variableSort1(3) :: Nil) :: Nil)
  val goal4: Task = Task("goal", isPrimitive = true, Nil, Nil, preconditions = Nil, effects = Nil)

}
