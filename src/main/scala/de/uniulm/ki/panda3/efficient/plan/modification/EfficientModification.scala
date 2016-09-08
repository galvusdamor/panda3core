package de.uniulm.ki.panda3.efficient.plan.modification

import de.uniulm.ki.panda3.efficient.csp.EfficientVariableConstraint
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.element.EfficientCausalLink
import de.uniulm.ki.panda3.efficient.plan.flaw.EfficientFlaw
import de.uniulm.ki.panda3.symbolic.PrettyPrintable

import scala.collection.mutable.ArrayBuffer


/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait EfficientModification extends PrettyPrintable{

  val plan: EfficientPlan
  val addedVariableConstraints   : Array[EfficientVariableConstraint] = Array()
  val addedVariableSorts         : Array[Int]                         = Array()
  val decomposedPlanStepsByMethod: Array[(Int, Int)]                  = Array()
  val resolvedFlaw: EfficientFlaw
  val addedCausalLinks: Array[EfficientCausalLink]              = Array()
  /** (type of new, parameters of new planstep, decomposed by method, parent in decomposition tree, ps in method subplan) */
  val addedPlanSteps  : Array[(Int, Array[Int], Int, Int, Int)] = Array()

  // in general insertion will be done completely free
  val insertInOrderingRelativeToPlanStep : Int = -1
  val insertedPlanStepsOrderingMatrix : Option[Array[Array[Byte]]] = None

  lazy val addedOrderings: Array[(Int, Int)] = {
    assert(plan != null, "Orderings can only be computed if connection to parent plan has not been severed")
    val buffer = new ArrayBuffer[(Int, Int)]()
    buffer appendAll nonInducedAddedOrderings

    var i = 0
    while (i < addedCausalLinks.length) {
      val producerTask = taskOfPlanStep(addedCausalLinks(i).producer)
      val consumerTask = taskOfPlanStep(addedCausalLinks(i).consumer)
      if (plan.domain.tasks(producerTask).isPrimitive && plan.domain.tasks(consumerTask).isPrimitive)
        buffer.append((addedCausalLinks(i).producer, addedCausalLinks(i).consumer))
      i += 1
    }

    buffer.toArray
  }

  private def taskOfPlanStep(ps: Int): Int = {
    assert(plan != null, "Tasks of plan steps can only be computed if connection to parent plan has not been severed")
    if (ps >= plan.firstFreePlanStepID) addedPlanSteps(ps - plan.firstFreePlanStepID)._1 else plan.planStepTasks(ps)
  }


  // here we compute all other necessary orderings, like the ones inherited from causal links
  // the orderings to init and goal will be added by the plan itself
  // this _MUST_ be lazy!!
  protected val nonInducedAddedOrderings: Array[(Int, Int)] = Array()

  lazy val decomposedPlanSteps: Array[Int] = {
    val result = new Array[Int](decomposedPlanStepsByMethod.length)
    var i = 0
    while (i < result.length) {
      result(i) = decomposedPlanStepsByMethod(i)._1
      i += 1
    }
    result
  }

  def severLinkToPlan: EfficientModification = {
    assert(plan != null)
    severLinkToPlan(resolvedFlaw.severLinkToPlan)
  }

  def severLinkToPlan(severedFlaw: EfficientFlaw): EfficientModification

  /** returns a detailed information about the object */
  override def longInfo: String = shortInfo

  /** returns a string that can be utilized to define the object */
  override def mediumInfo: String = shortInfo
}