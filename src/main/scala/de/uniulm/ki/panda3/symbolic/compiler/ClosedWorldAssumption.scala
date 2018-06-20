// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2018 the original author or authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package de.uniulm.ki.panda3.symbolic.compiler

import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.csp.Equal
import de.uniulm.ki.panda3.symbolic.domain.updates.{AddVariableConstraints, AddVariables, ExchangePlanSteps}
import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep


/**
  * applies the CWA to the initial state
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object ClosedWorldAssumption extends DomainTransformer[(Boolean, Set[String])] {

  def transform(inDomain: Domain, inPlan: Plan): (Domain, Plan) = transform(inDomain, inPlan, (true, Set()))

  /** takes a domain, an initial plan and some additional Information and transforms them */
  override def transform(domain: Domain, plan: Plan, config: (Boolean, Set[String])): (Domain, Plan) = {
    val (dontNegateUnnecessarily, predicateToKeep) = config
    val oldInit: PlanStep = plan.init

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
    val variablesToConstants: Map[Variable, Constant] = variablesForConstants map { _.swap }

    // find predicates which never occur negatively in the domain at all ... we don't need to apply the CWA to them
    val occurringNegativePredicatesWithArgumentVariables = ((domain.tasks ++ domain.hiddenTasks) flatMap { _.precondition.containedPredicatesWithSign }) ++ (domain.decompositionMethods
      collect {
      case SHOPDecompositionMethod(_, _, precondition, _, _) => precondition.containedPredicatesWithSign
    } flatten)

    val occurringNegativePredicates = occurringNegativePredicatesWithArgumentVariables map { case (p, _, s) => (p, s) } distinct
    val negativePredicatesWithPossibleArguments: Map[Predicate, Seq[Seq[Variable]]] =
      occurringNegativePredicatesWithArgumentVariables collect { case (p, vs, false) => (p, vs) } groupBy { _._1 } map { case (p, vss) => p -> (vss map { _._2 }) }
    val nonOccurringNegativePredicates = domain.predicates map { p => (p, false) } filterNot occurringNegativePredicates.contains map { _._1 } filterNot {
      p => predicateToKeep.exists(_.drop(1) == p.name)
    }


    // create the new initial plan step
    // build a set of all literals
    val allLiterals: Seq[Literal] = if (dontNegateUnnecessarily)
      (domain.predicates.toSet -- nonOccurringNegativePredicates) flatMap { case predicate =>
        val allPossibleInstantiations = predicate.instantiateWithVariables(variablesForConstants)

        // determine which arguments can actually occur negatively ...
        val allowedVariables = negativePredicatesWithPossibleArguments(predicate)

        // and take only those that may be useful
        allPossibleInstantiations filter { case Literal(_, _, parameter) => allowedVariables exists { possibleParameters =>
          parameter zip possibleParameters forall { case (actual, possible) => possible.sort.elements contains variablesToConstants(actual) }
        }
        }
      } toSeq
    else domain.predicates.distinct flatMap { _.instantiateWithVariables(variablesForConstants) }


    // remove all literals that already occur in the initial state
    val newCSP = plan.variableConstraints.update(AddVariables(newVariables)).update(AddVariableConstraints(newVariableConstraints))

    val allLiteralsWithConstantArgs = allLiterals map { l => (l, (l.predicate, l.parameterVariables map newCSP.getRepresentative)) }
    val oldInitEffectsWithConstantArgs = oldInit.substitutedEffects map { l => (l.predicate, l.parameterVariables map newCSP.getRepresentative) } toSet

    val notPresentLiterals = allLiteralsWithConstantArgs filterNot { case (_, i) => oldInitEffectsWithConstantArgs.contains(i) } map { _._1 }

    //allLiterals filterNot { groundedLiteral => oldInit.substitutedEffects exists { initEffect => (groundedLiteral =?= initEffect) (newCSP) } }

    val newEffects = notPresentLiterals map { _.negate }

    val oldInitSchema = oldInit.schema match {
      case rt: ReducedTask => rt
      case _               => noSupport(FORUMLASNOTSUPPORTED)
    }

    val newInitSchema: ReducedTask = ReducedTask(oldInit.schema.name, isPrimitive = true, oldInit.schema.parameters ++ newVariables,
                                                 oldInit.schema.parameters ++ newVariables,
                                                 oldInit.schema.parameterConstraints ++ newVariableConstraints, oldInitSchema.precondition,
                                                 And[Literal](newEffects ++ oldInitSchema.effect.conjuncts))
    val newInit: PlanStep = PlanStep(oldInit.id, newInitSchema, oldInit.arguments ++ newVariables)

    (domain, plan.update(AddVariables(newVariables)).update(AddVariableConstraints(newVariableConstraints)).update(ExchangePlanSteps(oldInit, newInit)))
  }
}
