package de.uniulm.ki.panda3.efficient.plan.flaw

import de.uniulm.ki.panda3.efficient.csp.EfficientVariableConstraint
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.modification.{EfficientInsertPlanStepWithLink, EfficientInsertCausalLink, EfficientModification}

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
    buffer.toArray
  }


  /** the number of tasks (newTasks) follows the assumption that the last newTask tasks of the plan are new */
  def updateToNewPlan(newPlan: EfficientPlan, newTasks: Int): EfficientOpenPrecondition = {
    val newResolvers = new ArrayBuffer[EfficientModification]()
    val flaw = EfficientOpenPrecondition(newPlan, this.planStep, this.preconditionIndex)
    // copy the old ones
    var i = 0
    while (i < resolver.length) {
      if (resolver(i).isInstanceOf[EfficientInsertCausalLink]) {
        val asInsertLink = resolver(i).asInstanceOf[EfficientInsertCausalLink]
        val newLink = asInsertLink.causalLink.addOffsetToPlanStepsIfGreaterThan(newTasks, plan.firstFreePlanStepID)
        newResolvers append EfficientInsertCausalLink(newPlan, flaw, newLink, asInsertLink.necessaryVariableConstraints)
      } else {
        val asInsertTask = resolver(i).asInstanceOf[EfficientInsertPlanStepWithLink]
        // create new variables ... adding tasks to a plan also adds variables
        val newParameters: Array[Int] = new Array[Int](asInsertTask.newPlanStep._2.length)
        var parameter = 0
        val parameterOffset = newPlan.firstFreeVariableID - plan.firstFreeVariableID
        while (parameter < newParameters.length) {
          newParameters(parameter) = asInsertTask.newPlanStep._2(parameter) + parameterOffset
          parameter += 1
        }
        val newPlanStep = (asInsertTask.newPlanStep._1, newParameters, asInsertTask.newPlanStep._3, asInsertTask.newPlanStep._4)

        // update the constraints
        val newConstraints: Array[EfficientVariableConstraint] = new Array(asInsertTask.necessaryVariableConstraints.length)
        var constraint = 0
        while (constraint < newConstraints.length) {
          newConstraints(constraint) = asInsertTask.necessaryVariableConstraints(constraint).addToVariableIndexIfGreaterEqualThen(parameterOffset, plan.firstFreeVariableID)
          constraint += 1
        }

        val newLink = asInsertTask.causalLink.addOffsetToPlanStepsIfGreaterThan(newTasks, plan.firstFreePlanStepID)
        newResolvers append EfficientInsertPlanStepWithLink(newPlan, flaw, newPlanStep, asInsertTask.parameterVariableSorts, newLink, newConstraints)
      }
      i += 1
    }
    // add new insert causal link modifications that become possible due to the new tasks
    newResolvers appendAll EfficientInsertCausalLink(newPlan, flaw, planStep, preconditionIndex, newPlan.planStepTasks.length - newTasks, newPlan.planStepTasks.length)
    flaw.precomputedResolver = Some(newResolvers.toArray)
    flaw
  }
}