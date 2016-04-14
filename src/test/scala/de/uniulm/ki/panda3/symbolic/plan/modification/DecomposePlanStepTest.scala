package de.uniulm.ki.panda3.symbolic.plan.modification

import de.uniulm.ki.panda3.symbolic.domain.{HasExampleProblem3, HasExampleProblem4}
import de.uniulm.ki.panda3.symbolic.plan.element.{OrderingConstraint, PlanStep}
import org.scalatest.FlatSpec

/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off magic.number
class DecomposePlanStepTest extends FlatSpec with HasExampleProblem3 with HasExampleProblem4 {


  // returns the single added plan step
  def checkAbstractTaskDecompositionBasic(decomposition: DecomposePlanStep): PlanStep = {
    // check whether the decomposition looks as expected
    assert(decomposition.addedPlanSteps.size == 1)
    val addedPlanStep = decomposition.addedPlanSteps.head
    // the new planstep
    assert(addedPlanStep.schema == task1)
    assert(addedPlanStep.arguments.size == task1.parameters.size) // i.e. 1
    assert(decomposition.setPlanStepDecomposedByMethod.size == 1)
    assert(decomposition.removedPlanSteps.isEmpty)

    // check the ordering
    assert(decomposition.addedOrderingConstraints.size == 2)
    assert(decomposition.addedOrderingConstraints.contains(OrderingConstraint(psInit1, addedPlanStep)))
    assert(decomposition.addedOrderingConstraints.contains(OrderingConstraint(addedPlanStep, psGoal1)))


    // nothing else should have been touched, except maybe causal links
    assert(decomposition.addedVariables.isEmpty)
    assert(decomposition.removedVariables.isEmpty)

    assert(decomposition.addedVariableConstraints.isEmpty)
    assert(decomposition.removedVariableConstraints.isEmpty)

    // return
    addedPlanStep
  }


  "Decomposition without causal links" must "be computable" in {
    val possibleDecompositions: Seq[DecomposePlanStep] = DecomposePlanStep(plan1WithoutCausalLinks, psAbstract1, decompositionMethod1)

    // there is only a single decomposition
    assert(possibleDecompositions.size == 1)
    val decomposition = possibleDecompositions.head

    checkAbstractTaskDecompositionBasic(decomposition)

    assert(decomposition.addedCausalLinks.isEmpty)
    assert(decomposition.removedCausalLinks.isEmpty)

  }

  it must "be applicable" in {
    val possibleDecompositions: Seq[DecomposePlanStep] = DecomposePlanStep(plan1WithoutCausalLinks, psAbstract1, decompositionMethod1)
    val decomposition = possibleDecompositions.head

    val decomposedPlan = plan1WithoutCausalLinks.modify(decomposition)

    assert(decomposedPlan.planSteps.size == 3)
    val newTaskOption = decomposedPlan.planSteps.find { _.schema == task1 }
    assert(newTaskOption.isDefined)
    val newTask = newTaskOption.get

    assert(decomposedPlan.variableConstraints.variables.forall { _.name.contains("instance") })
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
    assert(decomposition.removedCausalLinks.isEmpty)
    // they should look like this:
    assert(decomposition.addedCausalLinks.exists { ps => ps.producer == psInit1 && ps.consumer == addedPlanStep && ps.condition == psInit1.substitutedEffects.head })
    assert(decomposition.addedCausalLinks.exists { ps => ps.producer == addedPlanStep && ps.consumer == psGoal1 && ps.condition == psGoal1.substitutedPreconditions.head })
  }

  it must "prohibit decomposition if not inherited" in {
    val possibleDecompositionsNone: Seq[DecomposePlanStep] = DecomposePlanStep(plan1WithoutCausalLinks, psAbstract1, decompositionMethod2)
    assert(possibleDecompositionsNone.isEmpty)

    val possibleDecompositionsOne: Seq[DecomposePlanStep] = DecomposePlanStep(plan1WithOneCausalLinks, psAbstract1, decompositionMethod2)
    assert(possibleDecompositionsOne.isEmpty)
  }

  "Not provided causal links" must "be inherited" in {
    val possibleDecompositionsBoth: Seq[DecomposePlanStep] = DecomposePlanStep(plan1WithBothCausalLinks, psAbstract1, decompositionMethod1)
    assert(possibleDecompositionsBoth.size == 1)
    val decomposition = possibleDecompositionsBoth.head


    val addedPlanStep = checkAbstractTaskDecompositionBasic(decomposition)

    // two causal links should be created
    assert(decomposition.addedCausalLinks.size == 2)
    assert(decomposition.removedCausalLinks.isEmpty)
    // they should look like this:
    assert(decomposition.addedCausalLinks.exists { ps => ps.producer == psInit1 && ps.consumer == addedPlanStep && ps.condition == psInit1.substitutedEffects.head })
    assert(decomposition.addedCausalLinks.exists { ps => ps.producer == addedPlanStep && ps.consumer == psGoal1 && ps.condition == psGoal1.substitutedPreconditions.head })
  }


  "All possible decompositions" must "be found in simple cases" in {
    val possibleDecompositionsNone: Seq[DecomposePlanStep] = DecomposePlanStep(plan1WithBothCausalLinks, psAbstract1, domain3)
    assert(possibleDecompositionsNone.size == 2)
  }

  it must "be found if multiple possibilities exist (1 links)" in {
    val possibleDecompositionsNone: Seq[DecomposePlanStep] = DecomposePlanStep(plan2WithOneLink, psAbstract2, decompositionMethod3)
    assert(possibleDecompositionsNone.size == 2)

    assert(possibleDecompositionsNone forall { _.addedCausalLinks.size == 1 })
    assert(possibleDecompositionsNone forall { _.addedVariables.size == 4 })
    assert(possibleDecompositionsNone forall { _.addedPlanSteps.size == 4 })
    assert(possibleDecompositionsNone exists { ps => val cl = ps.addedCausalLinks.head; cl.producer == psInit2 && cl.consumer.schema.isPrimitive })
    assert(possibleDecompositionsNone exists { ps => val cl = ps.addedCausalLinks.head; cl.producer == psInit2 && !cl.consumer.schema.isPrimitive })
  }


  it must "be found if multiple possibilities exist (2 links)" in {
    val possibleDecompositionsNone: Seq[DecomposePlanStep] = DecomposePlanStep(plan2WithTwoLinks, psAbstract2, decompositionMethod3)
    assert(possibleDecompositionsNone.size == 4)

    assert(possibleDecompositionsNone forall { _.addedCausalLinks.size == 2 })
    assert(possibleDecompositionsNone forall { _.addedVariables.size == 4 })
    assert(possibleDecompositionsNone forall { _.addedPlanSteps.size == 4 })
    assert(possibleDecompositionsNone forall { d => val cls = d.addedCausalLinks.partition { _.condition.predicate == predicate1 }; cls._1.size == 1 && cls._2.size == 1 })
    assert(possibleDecompositionsNone forall { _.addedCausalLinks forall { _.producer == psInit2 } })

    for (p1 <- true :: false :: Nil; p2 <- true :: false :: Nil)
      assert(possibleDecompositionsNone exists { d => val cls = d.addedCausalLinks.partition { _.condition.predicate == predicate1 }
        cls._1.head.consumer.schema.isPrimitive == p1 && cls._2.head.consumer.schema.isPrimitive == p2
      })
  }

  it must "be found if none is possible" in {
    val possibleDecompositions = DecomposePlanStep(plan2WithTwoLinks, psAbstract2, decompositionMethodEpsilon)

    assert(possibleDecompositions.isEmpty)
  }

  "Epsilon methods" must "be handled correctly" in {
    val possibleDecompositions = DecomposePlanStep(plan2WithoutLink, psAbstract2, decompositionMethodEpsilon)

    assert(possibleDecompositions.size == 1)

    assert(possibleDecompositions forall { _.addedCausalLinks.isEmpty })
    assert(possibleDecompositions forall { _.addedPlanSteps.isEmpty })
    assert(possibleDecompositions forall { _.addedVariables.isEmpty })
    assert(possibleDecompositions forall { _.addedVariableConstraints.isEmpty })
    assert(possibleDecompositions forall { _.removedCausalLinks.isEmpty })
    assert(possibleDecompositions forall { _.removedPlanSteps.isEmpty })
    assert(possibleDecompositions forall { _.removedVariableConstraints.isEmpty })
    assert(possibleDecompositions forall { _.removedVariables.isEmpty })
  }
}