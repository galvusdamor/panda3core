package de.uniulm.ki.panda3.plan.modification

import de.uniulm.ki.panda3.csp.SymbolicCSP
import de.uniulm.ki.panda3.domain.{HasExampleDomain3, HasExampleDomain4}
import de.uniulm.ki.panda3.plan.SymbolicPlan
import de.uniulm.ki.panda3.plan.element.{CausalLink, OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.plan.ordering.SymbolicTaskOrdering
import org.scalatest.FlatSpec

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class DecomposePlanStepTest extends FlatSpec with HasExampleDomain3 with HasExampleDomain4 {

  val psInit1 = PlanStep(0, init, instance_variableSort1(1) :: Nil)
  val psGoal1 = PlanStep(1, goal1, instance_variableSort1(1) :: Nil)


  val psAbstract1    = PlanStep(2, abstractTask1, instance_variableSort1(1) :: Nil)
  val planPlanSteps1 = psInit1 :: psGoal1 :: psAbstract1 :: Nil

  val causalLinkInitAbstract = CausalLink(psInit1, psAbstract1, psInit1.substitutedEffects(0))
  val causalLinkAbstractGoal = CausalLink(psAbstract1, psGoal1, psGoal1.substitutedPreconditions(0))

  // create a plan  init| -> a1 -> |goal (without causal links)
  val plan1WithoutCausalLinks = SymbolicPlan(planPlanSteps1, Nil,
                                             SymbolicTaskOrdering(OrderingConstraint(psInit1, psAbstract1) :: OrderingConstraint(psAbstract1, psGoal1) :: Nil, planPlanSteps1),
                                             SymbolicCSP(Set(instance_variableSort1(1)), Nil), psInit1, psGoal1)

  // create a plan  init| -p1> a1 -> |goal (wit causal links)
  val plan1WithOneCausalLinks = SymbolicPlan(planPlanSteps1, causalLinkInitAbstract :: Nil,
                                             SymbolicTaskOrdering(OrderingConstraint(psInit1, psAbstract1) :: OrderingConstraint(psAbstract1, psGoal1) :: Nil, planPlanSteps1),
                                             SymbolicCSP(Set(instance_variableSort1(1)), Nil), psInit1, psGoal1)


  // create a plan  init| -p1> a1 -p1> |goal (wit causal links)
  val plan1WithBothCausalLinks = SymbolicPlan(planPlanSteps1, causalLinkInitAbstract :: causalLinkAbstractGoal :: Nil,
                                              SymbolicTaskOrdering(OrderingConstraint(psInit1, psAbstract1) :: OrderingConstraint(psAbstract1, psGoal1) :: Nil, planPlanSteps1),
                                              SymbolicCSP(Set(instance_variableSort1(1)), Nil), psInit1, psGoal1)


  val psInit2 = PlanStep(0, init4, instance_variableSort1(1) :: Nil)
  val psGoal2 = PlanStep(1, goal4, Nil)


  val psAbstract2    = PlanStep(2, abstractTask2, instance_variableSort1(1) :: Nil)
  val planPlanSteps2 = psInit2 :: psGoal2 :: psAbstract2 :: Nil

  val causalLinkInit2Abstract2 = CausalLink(psInit2, psAbstract2, psInit2.substitutedEffects(0))

  // create a plan  init| -> a1 -> |goal (without causal links)
  val plan2 = SymbolicPlan(planPlanSteps2, causalLinkInit2Abstract2 :: Nil, SymbolicTaskOrdering(OrderingConstraint.allBetween(psInit2, psGoal2, psAbstract2), planPlanSteps1),
                           SymbolicCSP(Set(instance_variableSort1(1)), Nil), psInit2, psGoal2)


  // returns the single added plan step
  def checkAbstractTaskDecompositionBasic(decomposition: DecomposePlanStep): PlanStep = {
    // check whether the decomposition looks as expected
    assert(decomposition.addedPlanSteps.size == 1)
    val addedPlanStep = decomposition.addedPlanSteps.head
    assert(addedPlanStep.schema == task1)
    assert(addedPlanStep.arguments.size == task1.parameters.size) // i.e. 1

    assert(decomposition.removedPlanSteps.size == 1)
    assert(decomposition.removedPlanSteps.head.schema == abstractTask1)

    // check the ordering
    assert(decomposition.addedOrderingConstraints.size == 2)
    assert(decomposition.addedOrderingConstraints.contains(OrderingConstraint(psInit1, addedPlanStep)))
    assert(decomposition.addedOrderingConstraints.contains(OrderingConstraint(addedPlanStep, psGoal1)))

    assert(decomposition.removedOrderingConstraints.size == 2)
    assert(decomposition.removedOrderingConstraints.contains(OrderingConstraint(psInit1, psAbstract1)))
    assert(decomposition.removedOrderingConstraints.contains(OrderingConstraint(psAbstract1, psGoal1)))

    // nothing else should have been touched, except maybe causal links
    assert(decomposition.addedVariables.size == 0)
    assert(decomposition.removedVariables.size == 0)

    assert(decomposition.addedVariableConstraints.size == 0)
    assert(decomposition.removedVariableConstraints.size == 0)

    // return
    addedPlanStep
  }


  "Decomposition without causal links" must "be computable" in {
    val possibleDecompositions: Seq[DecomposePlanStep] = DecomposePlanStep(plan1WithoutCausalLinks, psAbstract1, decompositionMethod1)

    // there is only a single decomposition
    assert(possibleDecompositions.size == 1)
    val decomposition = possibleDecompositions.head

    checkAbstractTaskDecompositionBasic(decomposition)

    assert(decomposition.addedCausalLinks.size == 0)
    assert(decomposition.removedCausalLinks.size == 0)

  }

  it must "be applicable" in {
    val possibleDecompositions: Seq[DecomposePlanStep] = DecomposePlanStep(plan1WithoutCausalLinks, psAbstract1, decompositionMethod1)
    val decomposition = possibleDecompositions.head

    val decomposedPlan = plan1WithoutCausalLinks.modify(decomposition)

    assert(decomposedPlan.planSteps.size == 3)
    val newTaskOption = decomposedPlan.planSteps.find {_.schema == task1}
    assert(newTaskOption != None)
    val newTask = newTaskOption.get

    assert(decomposedPlan.variableConstraints.variables.forall {_.name.contains("instance")})
    assert(decomposedPlan.variableConstraints.variables == plan1WithoutCausalLinks.variableConstraints.variables)
    assert(decomposedPlan.orderingConstraints.lt(psInit1, newTask))
    assert(decomposedPlan.orderingConstraints.lt(newTask, psGoal1))
  }

  "Provided causal links" must "be inherited" in {
    val possibleDecompositionsBoth: Seq[DecomposePlanStep] = DecomposePlanStep(plan1WithBothCausalLinks, psAbstract1, decompositionMethod2)
    assert(possibleDecompositionsBoth.size == 1)
    val decomposition = possibleDecompositionsBoth.head


    val addedPlanStep = checkAbstractTaskDecompositionBasic(decomposition)

    // two causal links should be created
    assert(decomposition.addedCausalLinks.size == 2)
    assert(decomposition.removedCausalLinks.size == 2)
    // they should look like this:
    assert(decomposition.addedCausalLinks.exists { ps => ps.producer == psInit1 && ps.consumer == addedPlanStep && ps.condition == psInit1.substitutedEffects(0) })
    assert(decomposition.addedCausalLinks.exists { ps => ps.producer == addedPlanStep && ps.consumer == psGoal1 && ps.condition == psGoal1.substitutedPreconditions(0) })
  }

  it must "prohibit decomposition if not inherited" in {
    val possibleDecompositionsNone: Seq[DecomposePlanStep] = DecomposePlanStep(plan1WithoutCausalLinks, psAbstract1, decompositionMethod2)
    assert(possibleDecompositionsNone.size == 0)

    val possibleDecompositionsOne: Seq[DecomposePlanStep] = DecomposePlanStep(plan1WithOneCausalLinks, psAbstract1, decompositionMethod2)
    assert(possibleDecompositionsOne.size == 0)
  }

  "Not provided causal links" must "be inherited" in {
    val possibleDecompositionsBoth: Seq[DecomposePlanStep] = DecomposePlanStep(plan1WithBothCausalLinks, psAbstract1, decompositionMethod1)
    assert(possibleDecompositionsBoth.size == 1)
    val decomposition = possibleDecompositionsBoth.head


    val addedPlanStep = checkAbstractTaskDecompositionBasic(decomposition)

    // two causal links should be created
    assert(decomposition.addedCausalLinks.size == 2)
    assert(decomposition.removedCausalLinks.size == 2)
    // they should look like this:
    assert(decomposition.addedCausalLinks.exists { ps => ps.producer == psInit1 && ps.consumer == addedPlanStep && ps.condition == psInit1.substitutedEffects(0) })
    assert(decomposition.addedCausalLinks.exists { ps => ps.producer == addedPlanStep && ps.consumer == psGoal1 && ps.condition == psGoal1.substitutedPreconditions(0) })
  }


  "All possible decompositions" must "be found in simple cases" in {
    val possibleDecompositionsNone: Seq[DecomposePlanStep] = DecomposePlanStep(plan1WithBothCausalLinks, psAbstract1, domain3)
    assert(possibleDecompositionsNone.size == 2)
  }

  // Does not work yet
  /*  it must "be found if multiple possibilities exist" in {
      val possibleDecompositionsNone: Seq[DecomposePlanStep] = DecomposePlanStep(plan2, psAbstract2, decompositionMethod3)
      assert(possibleDecompositionsNone.size == 2)
    }*/
}