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
trait HasExampleDomain3 extends HasExampleDomain2 {

  val abstractTask1: Task = Task("abstractTask_1", isPrimitive = false, variableSort1(7) :: Nil, Nil, preconditions = Literal(predicate1, isPositive = false, variableSort1(7) :: Nil) :: Nil,
                                 effects = Literal(predicate1, isPositive = true, variableSort1(7) :: Nil) :: Nil)


  val initTaskOfplanOfDecompositionMethod1: Task = Task("initM1", isPrimitive = true, variableSort1(8) :: Nil, Nil, preconditions = Nil,
                                                        effects = Literal(predicate1, isPositive = false, variableSort1(8) :: Nil) :: Nil)
  val goalTaskOfplanOfDecompositionMethod1: Task = Task("goalM1", isPrimitive = true, variableSort1(9) :: Nil, Nil,
                                                        preconditions = Literal(predicate1, isPositive = true, variableSort1(8) :: Nil) :: Nil, effects = Nil)

  val initOfPlanOfDecompositionMethod1          : PlanStep     = PlanStep(0, initTaskOfplanOfDecompositionMethod1, variableSort1(7) :: Nil)
  val goalOfPlanOfDecompositionMethod1          : PlanStep     = PlanStep(2, goalTaskOfplanOfDecompositionMethod1, variableSort1(7) :: Nil)
  val actualPlanStepOfPlanOfDecompositionMethod1: PlanStep     = PlanStep(1, task1, variableSort1(7) :: Nil)
  val planStepsOfPlanOfDecompositionMethod1                    = initOfPlanOfDecompositionMethod1 :: goalOfPlanOfDecompositionMethod1 :: actualPlanStepOfPlanOfDecompositionMethod1 :: Nil
  val taskOrderingOfPlanOfDecompositionMethod1  : TaskOrdering = SymbolicTaskOrdering(OrderingConstraint(initOfPlanOfDecompositionMethod1, actualPlanStepOfPlanOfDecompositionMethod1)
                                                                                        :: OrderingConstraint(actualPlanStepOfPlanOfDecompositionMethod1,
                                                                                                              goalOfPlanOfDecompositionMethod1) :: Nil, planStepsOfPlanOfDecompositionMethod1)
  val cspOfPlanOfDecompositionMethod1           : CSP          = SymbolicCSP(Set(variableSort1(7)), Nil)

  val planOfDecompositionMethod1: Plan = SymbolicPlan(planStepsOfPlanOfDecompositionMethod1, Nil, taskOrderingOfPlanOfDecompositionMethod1, cspOfPlanOfDecompositionMethod1,
                                                      initOfPlanOfDecompositionMethod1, goalOfPlanOfDecompositionMethod1)


  val decompositionMethod1: DecompositionMethod = DecompositionMethod(abstractTask1, planOfDecompositionMethod1)


  val domain3: Domain = Domain(sort1 :: Nil, constantSort1(1) :: constantSort1(2) :: Nil, predicate1 :: Nil, abstractTask1 :: task1 :: init :: goal1 :: Nil, decompositionMethod1 :: Nil, Nil)
}
