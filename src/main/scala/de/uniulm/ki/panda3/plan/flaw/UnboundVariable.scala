package de.uniulm.ki.panda3.plan.flaw

import de.uniulm.ki.panda3.csp.Variable
import de.uniulm.ki.panda3.domain.Domain
import de.uniulm.ki.panda3.plan.Plan
import de.uniulm.ki.panda3.plan.modification.{BindVariableToValue, Modification}

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class UnboundVariable(plan: Plan, variable: Variable) extends Flaw {
  override def resolvants(domain: Domain): Seq[Modification] = BindVariableToValue(plan, variable)
}
