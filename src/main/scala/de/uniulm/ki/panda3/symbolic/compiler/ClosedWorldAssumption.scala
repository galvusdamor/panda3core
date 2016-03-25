package de.uniulm.ki.panda3.symbolic.compiler

import de.uniulm.ki.panda3.symbolic.csp.Equal
import de.uniulm.ki.panda3.symbolic.domain.updates.{AddVariableConstraints, AddVariables, ExchangePlanStep}
import de.uniulm.ki.panda3.symbolic.domain.{GeneralTask, Domain, Task}
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep


/**
 * applies the CWA to the initial state
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
object ClosedWorldAssumption extends DomainTransformer[Unit] {

  def transform(inDomain: Domain, inPlan: Plan): (Domain, Plan) = transform(inDomain, inPlan, ())

  /** takes a domain, an initial plan and some additional Information and transforms them */
  override def transform(domain: Domain, plan: Plan, info: Unit): (Domain, Plan) = {
    val oldInit = plan.init

    // determine whether there are all variables for constants we possibly need
    val existstingVariablesForConstants: Map[Constant, Variable] = (oldInit.arguments collect { case v if plan.variableConstraints.getRepresentative(v).isConstant => plan.variableConstraints
      .getRepresentative(v).asInstanceOf[Constant] -> v
    }).toMap

    // generate all needed variables for constants
    val baseVarID = plan.getFirstFreeVariableID
    val newVariableConstraints = (domain.constants filterNot existstingVariablesForConstants.contains).zipWithIndex map { case (c, idx) =>
      val variable = Variable(baseVarID + idx, "var_for_const_" + c.name, Sort("sort_for_const_" + c.name, c :: Nil, Nil))
      Equal(variable, c)
    }
    val newVariables = newVariableConstraints map {
      _.left
    }

    // build the const->var map
    val variablesForConstants: Map[Constant, Variable] = existstingVariablesForConstants ++ (newVariableConstraints map { case Equal(v, c) => c.asInstanceOf[Constant] -> v})


    // create the new initial plan step
    // build a set of all literals
    val allLiterals: Seq[Literal] = domain.predicates flatMap {
      _.instantiate(domain, variablesForConstants)
    }

    // remove all literals that already occur in the initial state
    val newCSP = plan.variableConstraints.update(AddVariables(newVariables)).update(AddVariableConstraints(newVariableConstraints))
    val notPredentLiterals = allLiterals filterNot { groundedLiteral => oldInit.substitutedEffects exists { initEffect => (groundedLiteral =?= initEffect)(newCSP)}}
    val newEffects = notPredentLiterals map {
      _.negate
    }


    val newInitSchema: GeneralTask = GeneralTask(oldInit.schema.name, isPrimitive = true, oldInit.schema.parameters ++ newVariables,
      oldInit.schema.parameterConstraints ++ newVariableConstraints, oldInit.schema.precondition,
      And[Formula](newEffects :+ oldInit.schema.effect))
    val newInit: PlanStep = PlanStep(oldInit.id, newInitSchema, oldInit.arguments ++ newVariables, oldInit.decomposedByMethod, oldInit.parentInDecompositionTree)

    (domain, plan.update(AddVariables(newVariables)).update(AddVariableConstraints(newVariableConstraints)).update(ExchangePlanStep(oldInit, newInit)))
  }
}