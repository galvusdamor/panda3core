package de.uniulm.ki.panda3.efficient.plan.flaw

import de.uniulm.ki.panda3.efficient.csp.EfficientVariableConstraint
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.modification.{EfficientDecomposePlanStep, EfficientInsertPlanStepWithLink, EfficientInsertCausalLink, EfficientModification}
import de.uniulm.ki.util._

import scala.collection.mutable.ArrayBuffer

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientOpenPrecondition(plan: EfficientPlan, planStep: Int, preconditionIndex: Int) extends EfficientFlaw {

  private var precomputedResolver: Option[Array[EfficientModification]] = None


  override lazy val resolver: Array[EfficientModification] = if (precomputedResolver.isDefined) precomputedResolver.get
  else {
    val buffer = new ArrayBuffer[EfficientModification]()
    buffer appendAll EfficientInsertCausalLink(plan, this, planStep, preconditionIndex)
    buffer appendAll EfficientInsertPlanStepWithLink(plan, this, planStep, preconditionIndex)

    // TODO decompose only those plan steps that can lead to the necessary effect
    var possibleProducer = 2
    while (possibleProducer < plan.firstFreePlanStepID) {
      if (!plan.domain.tasks(plan.planStepTasks(possibleProducer)).isPrimitive && possibleProducer != planStep && plan.planStepDecomposedByMethod(possibleProducer) == -1)
        buffer appendAll EfficientDecomposePlanStep(plan, this, possibleProducer)
      possibleProducer += 1
    }
    buffer.toArray
  }

  def severLinkToPlan: EfficientOpenPrecondition = severLinkToPlan(dismissDecompositionModifications = false) // TODO

  def severLinkToPlan(dismissDecompositionModifications: Boolean): EfficientOpenPrecondition = {
    assert(plan != null)
    val severedFlaw = EfficientOpenPrecondition(null, planStep, preconditionIndex)

    val severedModifications = new ArrayBuffer[EfficientModification]
    var i = 0
    while (i < resolver.length) {
      if (!dismissDecompositionModifications || !resolver(i).isInstanceOf[EfficientDecomposePlanStep])
        severedModifications append resolver(i).severLinkToPlan(severedFlaw)
      i += 1
    }

    severedFlaw.precomputedResolver = Some(severedModifications.toArray)
    severedFlaw.planFirstFreePlanStepID = plan.firstFreePlanStepID
    severedFlaw.planFirstFreeVariableID = plan.firstFreeVariableID
    severedFlaw
  }


  def equalToSeveredFlaw(flaw: EfficientFlaw): Boolean = if (flaw.isInstanceOf[EfficientOpenPrecondition]) {
    val eop = flaw.asInstanceOf[EfficientOpenPrecondition]
    eop.planStep == planStep && eop.preconditionIndex == preconditionIndex
  } else false


  private var planFirstFreePlanStepID = -1
  private var planFirstFreeVariableID = -1

  /** the number of tasks (newTasks) follows the assumption that the last newTask tasks of the plan are new */
  def updateToNewPlan(newPlan: EfficientPlan, newTasks: Int, decomposedPlanSteps: Array[Int]): EfficientOpenPrecondition = {
    assert(planFirstFreePlanStepID != -1)
    assert(planFirstFreeVariableID != -1)
    val newResolvers = new ArrayBuffer[EfficientModification]()
    val flaw = EfficientOpenPrecondition(newPlan, this.planStep, this.preconditionIndex)
    // copy the old ones
    var i = 0
    while (i < resolver.length) {
      // TODO take the CSPs into account
      if (resolver(i).isInstanceOf[EfficientInsertCausalLink]) {
        val asInsertLink = resolver(i).asInstanceOf[EfficientInsertCausalLink]
        // if the planstep was decomposed the link cannot be inserted any more
        if (!arrayContains(decomposedPlanSteps, asInsertLink.causalLink.producer)) {
          val newLink = asInsertLink.causalLink.addOffsetToPlanStepsIfGreaterThan(newTasks, planFirstFreePlanStepID)
          newResolvers append EfficientInsertCausalLink(newPlan, flaw, newLink, asInsertLink.necessaryVariableConstraints)
        }
      } else if (resolver(i).isInstanceOf[EfficientInsertPlanStepWithLink]) {
        val asInsertTask = resolver(i).asInstanceOf[EfficientInsertPlanStepWithLink]
        // create new variables ... adding tasks to a plan also adds variables
        val newParameters: Array[Int] = new Array[Int](asInsertTask.newPlanStep._2.length)
        var parameter = 0
        val parameterOffset = newPlan.firstFreeVariableID - planFirstFreeVariableID
        while (parameter < newParameters.length) {
          newParameters(parameter) = asInsertTask.newPlanStep._2(parameter) + parameterOffset
          parameter += 1
        }
        val newPlanStep = (asInsertTask.newPlanStep._1, newParameters, asInsertTask.newPlanStep._3, asInsertTask.newPlanStep._4)

        // update the constraints
        val newConstraints: Array[EfficientVariableConstraint] = new Array(asInsertTask.necessaryVariableConstraints.length)
        var constraint = 0
        while (constraint < newConstraints.length) {
          newConstraints(constraint) = asInsertTask.necessaryVariableConstraints(constraint).addToVariableIndexIfGreaterEqualThen(parameterOffset, planFirstFreeVariableID)
          constraint += 1
        }

        val newLink = asInsertTask.causalLink.addOffsetToPlanStepsIfGreaterThan(newTasks, planFirstFreePlanStepID)
        newResolvers append EfficientInsertPlanStepWithLink(newPlan, flaw, newPlanStep, asInsertTask.parameterVariableSorts, newLink, newConstraints)
      } else {
        val asDecomposePlanStep = resolver(i).asInstanceOf[EfficientDecomposePlanStep]
        //TODO recompute these
        // TODO currently the efficient plan dismisses these !!!!!
      }
      i += 1
    }

    var possibleProducer = 2
    while (possibleProducer < newPlan.firstFreePlanStepID) {
      if (!newPlan.domain.tasks(newPlan.planStepTasks(possibleProducer)).isPrimitive && possibleProducer != planStep)
        newResolvers appendAll EfficientDecomposePlanStep(newPlan, flaw, possibleProducer)
      possibleProducer += 1
    }

    // add new insert causal link modifications that become possible due to the new tasks
    newResolvers appendAll EfficientInsertCausalLink(newPlan, flaw, planStep, preconditionIndex, newPlan.planStepTasks.length - newTasks, newPlan.planStepTasks.length)
    flaw.precomputedResolver = Some(newResolvers.toArray)
    flaw
  }

}