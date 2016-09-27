package de.uniulm.ki.panda3.symbolic.compiler

import de.uniulm.ki.panda3.symbolic.domain.{Task, ReducedTask, GeneralTask, Domain}
import de.uniulm.ki.panda3.symbolic.domain.updates.{ExchangeTask, ReduceTasks, ReduceFormula}
import de.uniulm.ki.panda3.symbolic.logic.And
import de.uniulm.ki.panda3.symbolic.plan.Plan

/**
  * Replaces all occurring formulas with simple formulas if possible
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object ToPlainFormulaRepresentation extends DomainTransformer[Unit] {

  def transform(domainAndPlan: (Domain, Plan)): (Domain, Plan) = transform(domainAndPlan, ())

  def transform(inDomain: Domain, inPlan: Plan): (Domain, Plan) = transform(inDomain, inPlan, ())

  override def transform(domain: Domain, plan: Plan, info: Unit): (Domain, Plan) = {
    // remove quantifiers
    val replaceTasks: Seq[(Task, Task)] = domain.tasks flatMap {
      case gt@GeneralTask(_, _, parameters, _, _, prec, eff) =>
        val (newPrec, precVars) = prec.compileQuantors()
        val (newEff, effVars) = eff.compileQuantors()
        val newTask = gt.copy(effect = newEff, precondition = newPrec, parameters = parameters ++ precVars ++ effVars)
        //if ((precVars ++ effVars).nonEmpty) (gt, newTask) :: Nil else Nil
        (gt, newTask) :: Nil
      case _ => Nil
    }

    val compiledDomain = domain update ExchangeTask(replaceTasks.toMap)
    val compiledPlan = plan update ExchangeTask(replaceTasks.toMap)

    // reduce all formulas
    val domReduced = compiledDomain update ReduceFormula()
    val planReduced = compiledPlan update ReduceFormula()

    (domReduced.update(ReduceTasks()), planReduced.update(ReduceTasks()))
  }
}