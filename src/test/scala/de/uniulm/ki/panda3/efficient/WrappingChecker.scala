package de.uniulm.ki.panda3.efficient

import de.uniulm.ki.panda3.efficient.domain.EfficientTask
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.element.EfficientCausalLink
import de.uniulm.ki.panda3.symbolic.domain.ReducedTask
import de.uniulm.ki.panda3.symbolic.logic.{Sort, Variable}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.CausalLink
import de.uniulm.ki.util._

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object WrappingChecker {


  def assertEqual(task: ReducedTask, efficientTask: EfficientTask, wrapper: Wrapping): Unit = {
    assert(task.isPrimitive == efficientTask.isPrimitive)
    assert(task.parameters.length == efficientTask.parameterSorts.length)
    task.parameters map { _.sort } zip efficientTask.parameterSorts foreach { case (symSort, effSort) => assert(effSort == wrapper.unwrap(symSort)) }
    assert(task.precondition.conjuncts.size == efficientTask.precondition.length)
    assert(task.effect.conjuncts.size == efficientTask.effect.length)


    task.precondition.conjuncts zip efficientTask.precondition foreach { case (symbolicLiteral, efficientLiteral) =>
      assert(symbolicLiteral.isPositive == efficientLiteral.isPositive)
      assert(wrapper.unwrap(symbolicLiteral.predicate) == efficientLiteral.predicate)
      assert(symbolicLiteral.parameterVariables.length == efficientLiteral.parameterVariables.length)
      symbolicLiteral.parameterVariables zip efficientLiteral.parameterVariables foreach { case (symbolicVariable, efficientVariable) =>
        assert(wrapper.unwrap(symbolicVariable, task) == efficientVariable)
      }
    }
  }


  def checkEqual(symbolicVariables: Seq[Variable], efficientVariables: Array[Int], variableMapping: Int => Variable): Boolean =
    if (symbolicVariables.size != efficientVariables.length) false
    else symbolicVariables zip efficientVariables forall { case (symVar, effVar) => variableMapping(effVar) == symVar }


  private val ILLEGAL_VARIABLE_NAME = "PANDA3_TEST_illegal_variable"


  private def checkEqual(effVar: Int, symVar: Variable, efficientPlan: EfficientPlan, symbolicPlan: Plan, efficientRepresentantsMapping: Int => Variable, wrapper: Wrapping): Boolean = {
    if (efficientPlan.variableConstraints.isRepresentativeAVariable(effVar))
      efficientRepresentantsMapping(efficientPlan.variableConstraints.getRepresentativeVariable(effVar)) == symbolicPlan.variableConstraints.getRepresentative(symVar)
    else
      wrapper.wrapConstant(efficientPlan.variableConstraints.getRepresentativeConstant(effVar)) == symbolicPlan.variableConstraints.getRepresentative(symVar)
  }

  /**
    * This might not work if the given plan belongs to a decomposition method
    */
  def assertEqual(symbolicPlan: Plan, efficientPlan: EfficientPlan, wrapper: Wrapping): Unit = {
    // general assertions that must always hold
    assert(efficientPlan.planStepTasks.length == symbolicPlan.planSteps.length)
    assert(efficientPlan.variableConstraints.variableSorts.length == symbolicPlan.variableConstraints.variables.size)
    assert(wrapper.wrapTask(efficientPlan.planStepTasks(0)) == symbolicPlan.init.schema)
    assert(wrapper.wrapTask(efficientPlan.planStepTasks(1)) == symbolicPlan.goal.schema)

    // iterate through all mappings between the plansteps of the symbolic and the efficient plan
    // and through all mappings between variables ...
    assert(allMappings(efficientPlan.planStepTasks.indices, symbolicPlan.planSteps) exists { psMappingPairs =>
      val psMapping = Map(psMappingPairs: _*)
      allMappings(efficientPlan.variableConstraints.variableSorts.indices, symbolicPlan.variableConstraints.variables.toSeq) exists { variableMappingPairs =>
        val variableMapping = Map(variableMappingPairs: _*)
        val inverseVariableMapping = Map(variableMappingPairs map { _.swap }: _*)

        // determine the representative mapping
        val efficientRepresentants: Map[Int, Variable] = ((variableMapping.keys collect { case i if efficientPlan.variableConstraints.isRepresentativeAVariable(i) =>
          efficientPlan.variableConstraints.getRepresentativeVariable(i)
        }).toSet[Int].toSeq map { i =>
          val symbolicRep = symbolicPlan.variableConstraints.getRepresentative(variableMapping(i))
          symbolicRep match {
            case variable: Variable => (i, variable)
            case _                  => (i, Variable(-1, ILLEGAL_VARIABLE_NAME, Sort("illegal", Nil, Nil)))
          }
        }).toMap

        val representativesOK = efficientRepresentants.values.toSet.size == efficientRepresentants.keys.size && (efficientRepresentants forall { case (effRep, symRep) =>
          efficientPlan.variableConstraints.isRepresentativeAVariable(inverseVariableMapping(symRep)) &&
            efficientPlan.variableConstraints.getRepresentativeVariable(inverseVariableMapping(symRep)) == effRep
        })


        // check equality of the CSP
        val cspEqual = variableMapping forall { case (effVar, symVar) =>
          // must have the same representative
          val sameRepresentative = checkEqual(effVar, symVar, efficientPlan, symbolicPlan, efficientRepresentants, wrapper)

          // must have same remaining domain
          val effRemainingDomain = efficientPlan.variableConstraints.getRemainingDomain(effVar)
          val symRemainingDomain = symbolicPlan.variableConstraints.reducedDomainOf(symVar)
          val sameDomain = effRemainingDomain.size == symRemainingDomain.size && (effRemainingDomain map wrapper.wrapConstant forall symRemainingDomain.contains)

          val effUnequalTo = efficientPlan.variableConstraints.getVariableUnequalTo(effVar)
          val symUnequalTo = symbolicPlan.variableConstraints.getUnequalVariables(symVar)
          val sameUnequal = effUnequalTo.size == symUnequalTo.size && (effUnequalTo forall { effVar => symUnequalTo contains efficientRepresentants(effVar) })

          // we don't test the actual sorts of the plans, as this would be terrible. However the tests performed are sufficient to ensure equality
          sameRepresentative && sameDomain && sameUnequal
        }

        // check equality of all plansteps
        val planStepsEqual = psMapping forall { case (effPS, symPS) =>
          val tasksEqual = efficientPlan.planStepTasks(effPS) == wrapper.unwrap(symPS.schema)
          val argumentsEqual = checkEqual(symPS.arguments, efficientPlan.planStepParameters(effPS), variableMapping)
          val decompositionMethodEqual = if (symPS.decomposedByMethod.isEmpty) efficientPlan.planStepDecomposedByMethod(effPS) == -1
          else wrapper.wrapDecompositionMethod(efficientPlan.planStepDecomposedByMethod(effPS)) == symPS.decomposedByMethod.get
          val parentPlanStepEqual = if (symPS.parentInDecompositionTree.isEmpty) efficientPlan.planStepParentInDecompositonTree(effPS) == -1
          else wrapper.wrapTask(efficientPlan.planStepParentInDecompositonTree(effPS)) == symPS.parentInDecompositionTree.get

          tasksEqual && argumentsEqual && decompositionMethodEqual && parentPlanStepEqual
        }

        // check ordering
        val sameOrdering = psMapping.keys forall { effPS1 => psMapping.keys forall { effPS2 =>
          efficientPlan.ordering.lt(effPS1, effPS2) == symbolicPlan.orderingConstraints.lt(psMapping(effPS1), psMapping(effPS2))
        }
        }

        // check causal links
        val sameCausalLinks = efficientPlan.causalLinks forall { case EfficientCausalLink(effProducer, effConsumer, effProducerLiteral, _) =>
          symbolicPlan.causalLinks exists { case CausalLink(symProducer, symConsumer, literal) =>
            val equalPlanSteps = psMapping(effProducer) == symProducer && psMapping(effConsumer) == symConsumer
            val effProducerTask = efficientPlan.domain.tasks(efficientPlan.planStepTasks(effProducer))
            val effLiteral = effProducerTask.effect(effProducerLiteral)
            val equalPredicates = wrapper.wrapPredicate(effLiteral.predicate) == literal.predicate
            val equalSign = effLiteral.isPositive == literal.isPositive
            val equalVariables = effProducerTask.getArgumentsOfLiteral(efficientPlan.planStepParameters(effProducer), effLiteral) zip literal
              .parameterVariables forall { case (effVar, symVar) => checkEqual(effVar, symVar, efficientPlan, symbolicPlan, efficientRepresentants, wrapper) }

            equalPlanSteps && equalPredicates && equalSign && equalVariables
          }
        }

        planStepsEqual && representativesOK && cspEqual && sameOrdering && sameCausalLinks
      }
    })
  }
}