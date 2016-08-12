package de.uniulm.ki.panda3.symbolic.domain

import de.uniulm.ki.panda3.symbolic.domain.datastructures.hierarchicalreachability.TaskDecompositionGraph

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class DomainPropertyAnalyser(domain: Domain, tdg: TaskDecompositionGraph) {

  private val tdgStrata = tdg.taskDecompositionGraph._1.condensation

  // no circles at all
  val isAcyclic: Boolean = tdgStrata.vertices forall { _.size == 1 }

  // recursion is only allowed through methods with a single task
  val isMostlyAcyclic: Boolean = tdgStrata.vertices forall { scc => if (scc.size == 1) true
  else scc collect { case x: GroundedDecompositionMethod => x } forall {
    _.decompositionMethod
      .subPlan.planStepsAndRemovedPlanStepsWithoutInitGoal.size == 1
  }
  }

  // is a method contains an abstract task it must be the last task
  val isRegular = tdg.reachableGroundMethods forall { case GroundedDecompositionMethod(method, _) =>
    val numberOfAbstracts = method.subPlan.planSteps count { _.schema.isAbstract }
    val isAbstractPlanStepLast = if (numberOfAbstracts == 0) true
    else {
      val abstractPlanStep = method.subPlan.planSteps find { _.schema.isAbstract } get

      method.subPlan.isLastPlanStep(abstractPlanStep)
    }

    numberOfAbstracts <= 1 && isAbstractPlanStepLast
  }


  // recursion is allowed, but every non-last task must be in a strictly lower strata
  val isTailRecursive = tdg.reachableGroundMethods forall { case groundMethod@GroundedDecompositionMethod(method, _) =>
    val plan = method.subPlan
    val nonLastPlanSteps = plan.planStepsWithoutInitGoal filterNot plan.isLastPlanStep

    val groundedAbstractTaskComponent = tdg.taskDecompositionGraph._1 getComponentOf groundMethod.groundAbstractTask

    nonLastPlanSteps forall { ps => !(groundedAbstractTaskComponent contains groundMethod.subPlanPlanStepsToGrounded(ps)) }
  }

  val isTotallyOrdered : Boolean = domain.decompositionMethods forall { _.subPlan.orderingConstraints.isTotalOrder() }
}