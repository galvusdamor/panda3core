package de.uniulm.ki.panda3.symbolic.domain

import de.uniulm.ki.panda3.symbolic.csp.{CSP, SymbolicCSP}
import de.uniulm.ki.panda3.symbolic.logic.{And, Literal}
import de.uniulm.ki.panda3.symbolic.plan.element.{OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.ordering.{SymbolicTaskOrdering, TaskOrdering}
import de.uniulm.ki.panda3.symbolic.plan.{Plan, SymbolicPlan}

/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off magic.number
trait HasExampleDomain4 extends HasExampleDomain2 {

  val task3: ReducedTask = ReducedTask("task3", isPrimitive = true, variableSort1(2) :: Nil, Nil,
                                       precondition = And[Literal](Literal(predicate1, isPositive = true, variableSort1(2) :: Nil) :: Nil), effect = And[Literal](Nil))
  val task4: ReducedTask = ReducedTask("task4", isPrimitive = false, variableSort1(3) :: Nil, Nil,
                                       precondition = And[Literal](Literal(predicate1, isPositive = true, variableSort1(3) :: Nil) :: Nil), effect = And[Literal](Nil))

  val task5: ReducedTask = ReducedTask("task3", isPrimitive = true, variableSort1(4) :: Nil, Nil,
                                       precondition = And[Literal](Literal(predicate2, isPositive = true, variableSort1(4) :: Nil) :: Nil), effect = And[Literal](Nil))
  val task6: ReducedTask = ReducedTask("task4", isPrimitive = false, variableSort1(5) :: Nil, Nil,
                                       precondition = And[Literal](Literal(predicate2, isPositive = true, variableSort1(5) :: Nil) :: Nil), effect = And[Literal](Nil))

  val abstractTask2: ReducedTask = ReducedTask("abstractTask_2", isPrimitive = false, variableSort1(7) :: Nil, Nil,
                                               precondition = And[Literal](Literal(predicate1, isPositive = true, variableSort1(7) :: Nil) ::
                                                                             Literal(predicate2, isPositive = true, variableSort1(7) :: Nil) :: Nil), effect = And[Literal](Nil))


  // decomposition method 3, with two tasks
  val initTaskOfPlanOfDecompositionMethod3: ReducedTask = ReducedTask("initM3", isPrimitive = true, variableSort1(8) :: Nil, Nil, precondition = And[Literal](Nil), effect = And[Literal](
    Literal(predicate1, isPositive = true, variableSort1(8) :: Nil) :: Literal(predicate2, isPositive = true, variableSort1(8) :: Nil) :: Nil))
  val goalTaskOfPlanOfDecompositionMethod3: ReducedTask = ReducedTask("goalM3", isPrimitive = true, Nil, Nil, precondition = And[Literal](Nil), effect = And[Literal](Nil))

  val initOfPlanOfDecompositionMethod3           : PlanStep     = PlanStep(0, initTaskOfPlanOfDecompositionMethod3, variableSort1(7) :: Nil)
  val goalOfPlanOfDecompositionMethod3           : PlanStep     = PlanStep(5, goalTaskOfPlanOfDecompositionMethod3, Nil)
  val actualPlanStep1OfPlanOfDecompositionMethod3: PlanStep     = PlanStep(1, task3, variableSort1(8) :: Nil)
  val actualPlanStep2OfPlanOfDecompositionMethod3: PlanStep     = PlanStep(2, task4, variableSort1(9) :: Nil)
  val actualPlanStep3OfPlanOfDecompositionMethod3: PlanStep     = PlanStep(3, task5, variableSort1(10) :: Nil)
  val actualPlanStep4OfPlanOfDecompositionMethod3: PlanStep     = PlanStep(4, task6, variableSort1(11) :: Nil)
  val planStepsOfPlanOfDecompositionMethod3                     = initOfPlanOfDecompositionMethod3 :: goalOfPlanOfDecompositionMethod3 :: actualPlanStep1OfPlanOfDecompositionMethod3 ::
    actualPlanStep2OfPlanOfDecompositionMethod3 :: actualPlanStep3OfPlanOfDecompositionMethod3 :: actualPlanStep4OfPlanOfDecompositionMethod3 :: Nil
  val taskOrderingOfPlanOfDecompositionMethod3   : TaskOrdering = SymbolicTaskOrdering(OrderingConstraint.allBetween(initOfPlanOfDecompositionMethod3, goalOfPlanOfDecompositionMethod3,
                                                                                                                     actualPlanStep1OfPlanOfDecompositionMethod3,
                                                                                                                     actualPlanStep2OfPlanOfDecompositionMethod3,
                                                                                                                     actualPlanStep3OfPlanOfDecompositionMethod3,
                                                                                                                     actualPlanStep4OfPlanOfDecompositionMethod3),
                                                                                       planStepsOfPlanOfDecompositionMethod3)
  val cspOfPlanOfDecompositionMethod3            : CSP          = SymbolicCSP(Set(variableSort1(7), variableSort1(8), variableSort1(9), variableSort1(10), variableSort1(11)), Nil)

  val planOfDecompositionMethod3: Plan                      = SymbolicPlan(planStepsOfPlanOfDecompositionMethod3, Nil, taskOrderingOfPlanOfDecompositionMethod3,
                                                                           cspOfPlanOfDecompositionMethod3, initOfPlanOfDecompositionMethod3, goalOfPlanOfDecompositionMethod3)
  /** a decomposition method without causal links */
  val decompositionMethod3      : SimpleDecompositionMethod = SimpleDecompositionMethod(abstractTask2, planOfDecompositionMethod3)

  /** an empty decomposition method */
  val decompositionMethodEpsilon: SimpleDecompositionMethod =
    SimpleDecompositionMethod(abstractTask2, SymbolicPlan(initOfPlanOfDecompositionMethod3 :: goalOfPlanOfDecompositionMethod3 :: Nil, Nil,
                                                          SymbolicTaskOrdering(OrderingConstraint.allBetween(initOfPlanOfDecompositionMethod3, goalOfPlanOfDecompositionMethod3),
                                                                               initOfPlanOfDecompositionMethod3 :: goalOfPlanOfDecompositionMethod3 :: Nil),
                                                          SymbolicCSP(Set(variableSort1(7)), Nil), initOfPlanOfDecompositionMethod3, goalOfPlanOfDecompositionMethod3))

  val init4: ReducedTask = ReducedTask("init", isPrimitive = true, variableSort1(3) :: Nil, Nil, precondition = And[Literal](Nil), effect = And[Literal](
    Literal(predicate1, isPositive = true, variableSort1(3) :: Nil) :: Literal(predicate2, isPositive = true, variableSort1(3) :: Nil) :: Nil))
  val goal4: ReducedTask = ReducedTask("goal", isPrimitive = true, Nil, Nil, precondition = And[Literal](Nil), effect = And[Literal](Nil))


  val domain4 = Domain(sort1 :: Nil, predicate1 :: predicate2 :: Nil, task1 :: task2 :: task3 :: task4 :: task5 :: task6 :: abstractTask2 :: Nil, decompositionMethod3 ::
    decompositionMethodEpsilon :: Nil, Nil)
}
