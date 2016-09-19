package de.uniulm.ki.panda3.symbolic.compiler

import de.uniulm.ki.panda3.symbolic.csp.Equal
import de.uniulm.ki.panda3.symbolic.domain.updates.{AddVariableConstraints, AddVariables, ExchangePlanSteps}
import de.uniulm.ki.panda3.symbolic.domain.{SHOPDecompositionMethod, GeneralTask, Domain, Task}
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep


/**
  * applies the CWA to the initial state
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object ClosedWorldAssumption extends DomainTransformer[Boolean] {

  def transform(inDomain: Domain, inPlan: Plan): (Domain, Plan) = transform(inDomain, inPlan, dontNegateUnnecessarily = true)

  /** takes a domain, an initial plan and some additional Information and transforms them */
  override def transform(domain: Domain, plan: Plan, dontNegateUnnecessarily: Boolean): (Domain, Plan) = {
    val oldInit = plan.init

    // determine whether there are all variables for constants we possibly need
    val existingVariablesForConstants: Map[Constant, Variable] = (oldInit.arguments collect { case v if plan.variableConstraints.getRepresentative(v).isConstant =>
      plan.variableConstraints.getRepresentative(v).asInstanceOf[Constant] -> v
    }).toMap

    // generate all needed variables for constants
    val baseVarID = plan.getFirstFreeVariableID
    val newVariableConstraints = (domain.constants filterNot existingVariablesForConstants.contains).zipWithIndex map { case (c, idx) =>
      val variable = Variable(baseVarID + idx, "var_for_const_" + c.name, Sort("sort_for_const_" + c.name, c :: Nil, Nil))
      Equal(variable, c)
    }
    val newVariables = newVariableConstraints map { _.left }

    // build the const->var map
    val variablesForConstants: Map[Constant, Variable] = existingVariablesForConstants ++ (newVariableConstraints map { case Equal(v, c) => c.asInstanceOf[Constant] -> v })

    // find predicates which never occur negatively in the domain at all ... we don't need to apply the CWA to them
    val occurringNegativePredicates = ((domain.tasks ++ domain.hiddenTasks) flatMap { _.precondition.containedPredicatesWithSign }) ++ (domain.decompositionMethods collect {
      case SHOPDecompositionMethod(_, _, precondition, _, _) => precondition.containedPredicatesWithSign
    } flatten)
    val nonOccurringNegativePredicates = domain.predicates map { p => (p, false) } filterNot occurringNegativePredicates.contains map { _._1 }

    // create the new initial plan step
    // build a set of all literals
    val allLiterals: Seq[Literal] = if (dontNegateUnnecessarily)
      (domain.predicates.toSet -- nonOccurringNegativePredicates) flatMap { _.instantiateWithVariables(variablesForConstants) } toSeq
    else domain.predicates.distinct flatMap { _.instantiateWithVariables(variablesForConstants) }


    // remove all literals that already occur in the initial state
    val newCSP = plan.variableConstraints.update(AddVariables(newVariables)).update(AddVariableConstraints(newVariableConstraints))

    val allLiteralsWithConstantArgs = allLiterals map { l => (l, (l.predicate, l.parameterVariables map newCSP.getRepresentative)) }
    val oldInitEffectsWithConstantArgs = oldInit.substitutedEffects map { l => (l.predicate, l.parameterVariables map newCSP.getRepresentative) } toSet

    val notPresentLiterals = allLiteralsWithConstantArgs filterNot { case (_, i) => oldInitEffectsWithConstantArgs.contains(i) } map { _._1 }

    //allLiterals filterNot { groundedLiteral => oldInit.substitutedEffects exists { initEffect => (groundedLiteral =?= initEffect) (newCSP) } }

    val newEffects = notPresentLiterals map { _.negate }

    val newInitSchema: GeneralTask = GeneralTask(oldInit.schema.name, isPrimitive = true, oldInit.schema.parameters ++ newVariables,
                                                 oldInit.schema.parameters ++ newVariables,
                                                 oldInit.schema.parameterConstraints ++ newVariableConstraints, oldInit.schema.precondition,
                                                 And[Formula](newEffects :+ oldInit.schema.effect))
    val newInit: PlanStep = PlanStep(oldInit.id, newInitSchema, oldInit.arguments ++ newVariables)

    (domain, plan.update(AddVariables(newVariables)).update(AddVariableConstraints(newVariableConstraints)).update(ExchangePlanSteps(oldInit, newInit)))
  }
}