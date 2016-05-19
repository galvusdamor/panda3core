package de.uniulm.ki.panda3.symbolic.plan.modification

import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.csp._
import de.uniulm.ki.panda3.symbolic.domain.{SimpleDecompositionMethod, DecompositionMethod, Domain}
import de.uniulm.ki.panda3.symbolic.logic.Variable
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{CausalLink, OrderingConstraint, PlanStep}

/**
  * A modification which decomposes a given plan step using a decomposition method
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class DecomposePlanStep(decomposedPS: PlanStep, nonPresentDecomposedPS: PlanStep, newSubPlan: Plan, newVariableConstraints: Seq[VariableConstraint],
                             inheritedCausalLinks: Seq[CausalLink], originalDecompositionMethod: DecompositionMethod, newPlanStepsToOldOnes: Map[PlanStep, PlanStep], plan: Plan)
  extends Modification {

  // internal variables for convenience
  private val init = newSubPlan.init
  private val goal = newSubPlan.goal

  // causal links in the method lining either init or goal
  private val initCausalLinks = newSubPlan.causalLinks filter { case CausalLink(p, c, _) => p == init }
  private val goalCausalLinks = newSubPlan.causalLinks filter { case CausalLink(p, c, _) => c == goal }

  // remove init and goal task of the subplan
  override def addedPlanSteps: Seq[PlanStep] = newSubPlan.planSteps filter { p => p != init && p != goal }

  // new variables
  override def addedVariables: Seq[Variable] = (newSubPlan.variableConstraints.variables -- decomposedPS.arguments).toSeq

  // variable constraints from the plan, and those forcing parameters of the decomposed task in the plan and in the method to be equal
  override def addedVariableConstraints: Seq[VariableConstraint] = newSubPlan.variableConstraints.constraints ++ newVariableConstraints

  override def nonInducedAddedOrderingConstraints: Seq[OrderingConstraint] = (newSubPlan.orderingConstraints.originalOrderingConstraints filter { case OrderingConstraint(b, a) =>
    b != init && b != goal && a != init && a != goal
  }) ++ (plan.orderingConstraints.originalOrderingConstraints flatMap {
    case OrderingConstraint(p, `decomposedPS`) => addedPlanSteps map { OrderingConstraint(p, _) }
    case OrderingConstraint(`decomposedPS`, p) => addedPlanSteps map { OrderingConstraint(_, p) }
    case _                                     => Nil
  })


  override def addedCausalLinks: Seq[CausalLink] = (newSubPlan.causalLinks filter { case cl => !cl.containsOne(init, goal) }) ++ inheritedCausalLinks

  override def setParentOfPlanSteps: Seq[(PlanStep, (PlanStep, PlanStep))] = addedPlanSteps map { ps => (ps, (decomposedPS, newPlanStepsToOldOnes(ps))) }

  override def setPlanStepDecomposedByMethod: Seq[(PlanStep, DecompositionMethod)] = (decomposedPS, originalDecompositionMethod) :: Nil
}

object DecomposePlanStep {

  def apply(plan: Plan, decomposedPS: PlanStep, domain: Domain): Seq[DecomposePlanStep] = domain.decompositionMethods flatMap { method =>
    if (!method.isInstanceOf[SimpleDecompositionMethod]) noSupport(NONSIMPLEMETHOD)
    apply(plan, decomposedPS, method.asInstanceOf[SimpleDecompositionMethod])
  }

  def apply(currentPlan: Plan, decomposedPS: PlanStep, method: SimpleDecompositionMethod): Seq[DecomposePlanStep] =
    if (decomposedPS.schema != method.abstractTask) Nil
    else {
      // create a new instance of the just decomposed planstep
      val nonPresentDecomposedPS = PlanStep(decomposedPS.id, decomposedPS.schema, decomposedPS.arguments)

      val firstFreePlanStepID = currentPlan.getFirstFreePlanStepID
      val firstFreeVariableID = currentPlan.getFirstFreeVariableID

      val decomposedAbstractTasksParameterSubstitution = PartialSubstitution(method.abstractTask.parameters, decomposedPS.arguments)

      // copy the plan to get new variable and plan step ids
      val (copiedPlan, _, planStepMapping) = method.subPlan.newInstance(firstFreePlanStepID, firstFreeVariableID, decomposedAbstractTasksParameterSubstitution, nonPresentDecomposedPS)

      // compute the first version of the CSP of the new plan
      val joinedCSP: CSP = currentPlan.variableConstraints.addVariables((copiedPlan.variableConstraints.variables -- decomposedPS.arguments).toSeq)
        .addConstraints(copiedPlan.variableConstraints.constraints)

      // the joinedCSP does not take constraints into account which are imposed by the parameter types of the plans tasks
      if (joinedCSP.isSolvable.contains(false) || method.subPlan.variableConstraints.isSolvable.contains(false)) Nil
      else {
        // causal links handling -> in pairs (ingoing, outgoing) links
        // causal links from and to init and goal of the methods subplan (i.e. those that _must_ be respected)
        val methodSpecifiedCausalLinks = copiedPlan.causalLinks filter { cl => cl.containsOne(copiedPlan.init, copiedPlan.goal) } partition { _.producer == copiedPlan.init }
        // pre-exisiting causal links involving the task to be decomposed
        val causalLinksWithDecomposedPlanStep = currentPlan.causalLinks filter { _.contains(decomposedPS) } partition { _.consumer == decomposedPS }

        // compute which plan steps are directly inherited, i.e. those specified by the init or goal of the plan
        // note, that this _cannot_ introduce new variable constraints, iff the subplan is a valid plan
        // first compute an auxiliary data-structure
        val ingoingLinksWithMatchingInner = causalLinksWithDecomposedPlanStep._1 map { case cl@CausalLink(_, _, precondition) =>
          val effectLiteralOfSubPlanInit = copiedPlan.init.substitutedEffects(decomposedPS.indexOfPrecondition(precondition, currentPlan.variableConstraints))

          (cl, methodSpecifiedCausalLinks._1 find { case CausalLink(_, _, effect) => (effectLiteralOfSubPlanInit =?= effect) (copiedPlan.variableConstraints) })
        }
        // compute the new links created by direct inheritance
        val directlyInheritedLinksIngoing: Seq[CausalLink] = ingoingLinksWithMatchingInner collect { case (ingoingCL, Some(innerCL)) => CausalLink(ingoingCL.producer, innerCL.consumer,
                                                                                                                                                   ingoingCL.condition)
        }
        // and separate those remaining
        val remainingLinksIngoing = ingoingLinksWithMatchingInner collect { case (cl, None) => cl }


        // the same for outgoing links
        val outgoingLinksWithMatchingInner = causalLinksWithDecomposedPlanStep._2 map { case cl@CausalLink(_, _, effect) =>
          val preconditionLiteralOfSubPlanGoal = copiedPlan.goal.substitutedPreconditions(decomposedPS.indexOfEffect(effect, currentPlan.variableConstraints))

          (cl, methodSpecifiedCausalLinks._2 find { case CausalLink(_, _, precondition) => (preconditionLiteralOfSubPlanGoal =?= precondition) (copiedPlan.variableConstraints) })
        }
        val directlyInheritedLinksOutgoing: Seq[CausalLink] = outgoingLinksWithMatchingInner collect { case (outgoingCL, Some(innerCL)) => CausalLink(innerCL.producer, outgoingCL.consumer,
                                                                                                                                                      outgoingCL.condition)
        }
        // and separate those remaining
        val remainingLinksOutgoing = outgoingLinksWithMatchingInner collect { case (cl, None) => cl }



        // if not all specified causal links can be inherited, this is not a legal decomposition
        if (directlyInheritedLinksIngoing.size != methodSpecifiedCausalLinks._1.size || directlyInheritedLinksOutgoing.size != methodSpecifiedCausalLinks._2.size)
          Nil
        else {
          // compute all possible ways to inherit the ingoing causal links
          def generateAllPossibleInheritances(ingoingLinks: Seq[CausalLink], outgoingLinks: Seq[CausalLink]): Seq[(Seq[CausalLink], CSP, Seq[VariableConstraint])] =
            if (ingoingLinks == Nil && outgoingLinks == Nil) (Nil, joinedCSP, Nil) :: Nil // list containing an empty list
            else {
              val cl = if (ingoingLinks != Nil) ingoingLinks.head else outgoingLinks.head
              val recursionIngoing = if (ingoingLinks != Nil) ingoingLinks.drop(1) else ingoingLinks
              val recursionOutgoing = if (ingoingLinks != Nil) outgoingLinks else outgoingLinks.drop(1)

              val possibleLinks = generateAllPossibleInheritances(recursionIngoing, recursionOutgoing)


              // compute all possibilities to add one link. This list still contains impossible inheritances signaled by an unsolvable CSP
              val possibilitiesToAddLink: Seq[(Seq[CausalLink], PlanStep, CSP, Seq[VariableConstraint])] = possibleLinks flatMap { case (links, csp, constraints) => copiedPlan
                .planStepsWithoutInitGoal flatMap {
                // if ingoing != Nil, then such a link was chosen, i.e. it needs to be connected to a precondition of some task in the sub-plan
                ps => (if (ingoingLinks != Nil) ps.substitutedPreconditions else ps.substitutedEffects) map { literal =>
                  (cl.condition #?# literal) (csp) match {
                    case None      => (links, ps, UnsolvableCSP, Nil)
                    case Some(mgu) => (links, ps, csp.addConstraints(mgu), constraints ++ mgu)
                  }
                }
              }
              }

              possibilitiesToAddLink collect { case (links, ps, csp, constraints) if !csp.isSolvable.contains(false) =>
                val newLink = if (ingoingLinks != Nil) CausalLink(cl.producer, ps, cl.condition) else CausalLink(ps, cl.consumer, cl.condition)
                (links :+ newLink, csp, constraints)
              }
            }


          generateAllPossibleInheritances(remainingLinksIngoing, remainingLinksOutgoing) map { case (links, _, linkConstraints) =>
            val causalLinks: Seq[CausalLink] = directlyInheritedLinksIngoing ++ directlyInheritedLinksOutgoing ++ links
            DecomposePlanStep(decomposedPS, nonPresentDecomposedPS, copiedPlan, linkConstraints, causalLinks, method, planStepMapping, currentPlan)
          }
        }
      }
    }

}