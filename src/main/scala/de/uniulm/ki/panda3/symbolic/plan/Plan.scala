package de.uniulm.ki.panda3.symbolic.plan

import de.uniulm.ki.panda3.symbolic.PrettyPrintable
import de.uniulm.ki.panda3.symbolic.csp.{CSP, Substitution}
import de.uniulm.ki.panda3.symbolic.domain.{DecompositionMethod, DomainUpdatable}
import de.uniulm.ki.panda3.symbolic.domain.updates.DomainUpdate
import de.uniulm.ki.panda3.symbolic.logic.Variable
import de.uniulm.ki.panda3.symbolic.plan.element.{CausalLink, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.flaw._
import de.uniulm.ki.panda3.symbolic.plan.modification.Modification
import de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering
import de.uniulm.ki.panda3.symbolic.search.{IsFlawAllowed, IsModificationAllowed}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait Plan extends DomainUpdatable with PrettyPrintable {

  lazy val flaws: Seq[Flaw] = {
    val hardFlaws = causalThreats ++ openPreconditions ++ abstractPlanSteps // ++ notInsertedByDecomposition
    if (hardFlaws.isEmpty) unboundVariables else hardFlaws
  } filter isFlawAllowed

  lazy val planStepsWithoutInitGoal: Seq[PlanStep] = planSteps filter { ps => ps != init && ps != goal }

  lazy val planStepsAndRemovedPlanStepsWithoutInitGoal: Seq[PlanStep] = planStepsAndRemovedPlanSteps filter { ps => ps != init && ps != goal }

  lazy val initAndGoal = init :: goal :: Nil

  lazy val planStepsAndRemovedWithInitAndGoalFirst = (init  :: goal :: Nil ) ++ planStepsAndRemovedPlanStepsWithoutInitGoal

  val isModificationAllowed: IsModificationAllowed
  val isFlawAllowed        : IsFlawAllowed

  /** all abstract plan steps */
  val abstractPlanSteps: Seq[AbstractPlanStep]

  /** all causal threads in this plan */
  val causalThreats: Seq[CausalThreat]

  /** all open preconditions in this plan */
  val openPreconditions: Seq[OpenPrecondition]

  /** all variables which are not bound to a constant, yet */
  val unboundVariables: Seq[UnboundVariable]

  /** all plansteps that have to parent in the decomposition tree */
  val notInsertedByDecomposition: Seq[NotInsertedByDecomposition]

  /* convenience methods to determine usable IDs */
  lazy val getFirstFreePlanStepID: Int = 1 + (planSteps foldLeft 0) { case (m, ps: PlanStep) => math.max(m, ps.id) }
  lazy val getFirstFreeVariableID: Int = 1 + (variableConstraints.variables foldLeft 0) { case (m, v: Variable) => math.max(m, v.id) }

  def planStepsAndRemovedPlanSteps: Seq[PlanStep]

  def planSteps: Seq[PlanStep]

  def causalLinks: Seq[CausalLink]

  def orderingConstraints: TaskOrdering

  def variableConstraints: CSP

  def init: PlanStep

  def goal: PlanStep

  def planStepDecomposedByMethod: Map[PlanStep, DecompositionMethod]

  /** first entry is the parent, the second respective plan step in the methods subplan */
  def planStepParentInDecompositionTree: Map[PlanStep, (PlanStep, PlanStep)]

  def isPresent(planStep: PlanStep): Boolean = !planStepDecomposedByMethod.contains(planStep)

  /** returns (if possible), whether this plan can be refined into a solution or not */
  def isSolvable: Option[Boolean]

  def modify(modification: Modification): Plan

  /** returns a completely new instantiated version of the current plan. This can e.g. be used to clone subplans of [[de.uniulm.ki.panda3.symbolic.domain.DecompositionMethod]]s. */
  def newInstance(firstFreePlanStepID: Int, firstFreeVariableID: Int, partialSubstitution: Substitution[Variable] = Substitution[Variable](Nil, Nil), parentPlanStep: PlanStep): (Plan,
    Substitution[Variable], Map[PlanStep, PlanStep])

  override def update(domainUpdate: DomainUpdate): Plan

  /** returns a short information about the object */
  override def shortInfo: String = (planSteps map { "PS " + _.mediumInfo }).mkString("\n") + "\n" + orderingConstraints.shortInfo + "\n" + (causalLinks map { _.longInfo }).mkString("\n") +
    "\n" + variableConstraints.constraints.mkString("\n")

  /** returns a string that can be utilized to define the object */
  override def mediumInfo: String = shortInfo

  /** returns a more detailed information about the object */
  override def longInfo: String = shortInfo
}