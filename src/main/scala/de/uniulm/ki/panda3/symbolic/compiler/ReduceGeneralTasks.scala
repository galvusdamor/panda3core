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

import de.uniulm.ki.panda3.symbolic.csp.CSP
import de.uniulm.ki.panda3.symbolic.domain.updates._
import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering
import de.uniulm.ki.panda3.symbolic.sat.additionalConstraints.LTLTrue
import de.uniulm.ki.panda3.symbolic.search.{NoFlaws, NoModifications}

/**
  * @author Mario Schmautz (mario.schmautz@uni-ulm.de)
  */
object ReduceGeneralTasks extends DomainTransformer[Unit] {

  def transform(domainAndPlan: (Domain, Plan)): (Domain, Plan) = transform(domainAndPlan, ())

  def transform(inDomain: Domain, inPlan: Plan): (Domain, Plan) = transform(inDomain, inPlan, ())

  override def transform(inDomain: Domain, inPlan: Plan, info: Unit): (Domain, Plan) = {
    var generalTasks = filterGeneralTasks(inDomain.tasks ++ artificialTasks(inDomain, inPlan))

    val replacements: Map[Task, Task] = (generalTasks map { gt: GeneralTask =>
      (gt, gt.copy(effect = moveNegationsInwards(gt.effect), precondition = moveNegationsInwards(gt.precondition)))
    }).toMap

    var domain = inDomain update SetExpandVariableConstraintsInPlans(dontExpand = true) update ExchangeTask(replacements)
    var plan = inPlan update SetExpandVariableConstraintsInPlans(dontExpand = true) update ExchangeTask(replacements)

    generalTasks = filterGeneralTasks(domain.tasks ++ artificialTasks(domain, plan))
    while (generalTasks.nonEmpty) {
      val (taskReplace, anyThing) = generalTasks map { gt => (gt, simplification(gt)) } partition { case (_, Left(_)) => true; case _ => false }
      val replaceMap: Map[Task, Task] = taskReplace map { case (gt, Left(newGT)) => gt -> newGT } toMap

      domain = domain update ExchangeTask(replaceMap)
      plan = plan update ExchangeTask(replaceMap)

      anyThing foreach { case (gt, Right(simp)) =>
        val (d, p, _) = simp(domain, plan)
        domain = d
        plan = p
      }
      generalTasks = filterGeneralTasks(domain.tasks ++ artificialTasks(domain, plan))
    }

    (domain update ReduceTasks() update SetExpandVariableConstraintsInPlans(dontExpand = false), plan update ReduceTasks() update SetExpandVariableConstraintsInPlans(dontExpand = false))
  }

  private def artificialTasks(domain: Domain, problem: Plan): Seq[Task] = {
    Seq(problem.init.schema, problem.goal.schema)
  }

  private def simplePlan(task: Task, initAndGoalArgs : Seq[Variable]): Plan = {
    val init = GeneralTask(
                            name = "init(simplePlan)",
                            isPrimitive = true,
                            parameters = initAndGoalArgs,
                            artificialParametersRepresentingConstants = Nil,
                            parameterConstraints = Nil,
                            precondition = And(Nil),
                            effect = And(Nil)
                          )
    val goal = GeneralTask(
      name = "goal(simplePlan)",
      isPrimitive = true,
      parameters = initAndGoalArgs,
      artificialParametersRepresentingConstants = Nil,
      parameterConstraints = Nil,
      precondition = And(Nil),
      effect = And(Nil)
    )
    val psInit = PlanStep(id = 0, schema = init, arguments = initAndGoalArgs)
    val psTask = PlanStep(id = 1, schema = task, arguments = task.parameters)
    val psGoal = PlanStep(id = 2, schema = goal, arguments = initAndGoalArgs)
    val planSteps = Seq(psInit, psTask, psGoal)
    val orderingConstraints = Seq(OrderingConstraint(psInit, psTask), OrderingConstraint(psTask, psGoal))
    Plan(
          planStepsAndRemovedPlanSteps = planSteps,
          causalLinksAndRemovedCausalLinks = Nil,
          orderingConstraints = TaskOrdering(originalOrderingConstraints = orderingConstraints, tasks = planSteps),
          parameterVariableConstraints = CSP(variables = task.parameters.toSet, constraints = Nil),
          init = psInit,
          goal = psGoal,
          isModificationAllowed = NoModifications,
          isFlawAllowed = NoFlaws,
          planStepDecomposedByMethod = Map(),
          planStepParentInDecompositionTree = Map(),
          dontExpandVariableConstraints = false,
          ltlConstraint = LTLTrue)
  }

  private final case class Simplification(original: Task, replacement: Task, newTasks: Seq[Task], newMethods: Seq[DecompositionMethod]) {
    def apply(domain: Domain, plan: Plan): (Domain, Plan, Seq[Task]) = {
      val newDomain = domain update ExchangeTask(Map(original -> replacement)) update AddTask(newTasks) update AddMethod(newMethods)
      val newPlan = plan update ExchangeTask(Map(original -> replacement))
      (newDomain, newPlan, newTasks :+ replacement)
    }
  }

  private def disjunctionSimplification(gt: GeneralTask, f: Or[Formula], rest: Formula): Simplification = {
    val replacement = gt.copy(isPrimitive = false, precondition = And(Nil), effect = And(Nil))
    val newTasks = f.disjuncts.zipWithIndex map { case (g, i) =>
      gt.copy(name = s"${gt.name }__DISJUNCT-${i }", precondition = join(g, rest))
    }
    val newMethods = newTasks map { t =>
      SimpleDecompositionMethod(replacement, simplePlan(t, replacement.parameters), s"M-${t.name }")
    }
    Simplification(gt, replacement, newTasks, newMethods)
  }

  private def conditionalSimplification(gt: GeneralTask, f: When, rest: Formula): Simplification = {
    val replacement = gt.copy(isPrimitive = false, precondition = And(Nil), effect = And(Nil))
    val t1 = gt.copy(
                      name = s"${gt.name }__ANTECEDENT",
                      precondition = join(moveNegationsInwards(Not(f.left)), gt.precondition),
                      effect = rest)
    val t2 = gt.copy(
                      name = s"${gt.name }__CONSEQUENT__",
                      precondition = join(f.left, gt.precondition),
                      effect = join(f.right, rest))
    val m1 = SimpleDecompositionMethod(replacement, simplePlan(t1, replacement.parameters), s"M-${t1.name }")
    val m2 = SimpleDecompositionMethod(replacement, simplePlan(t2, replacement.parameters), s"M-${t2.name }")
    Simplification(gt, replacement, Seq(t1, t2), Seq(m1, m2))
  }

  private def existentialSimplification(gt: GeneralTask, f: Exists, rest: Formula): Simplification = {
    val v = f.v.copy(name = s"${f.v.name }__EXISTENTIAL-${Variable.nextFreeVariableID() }")
    val replacement = gt.copy(
                               precondition = join(f.formula update ExchangeVariable(f.v, v), rest),
                               parameters = gt.parameters :+ v
                             )
    Simplification(gt, replacement, Nil, Nil)
  }

  private def universalSimplification(gt: GeneralTask, f: Forall, rest: Formula, isPrec: Boolean): Simplification = {
    val vs = f.v.sort.allElements map { c =>
      f.v.copy(
                name = s"${f.v.name }__UNIVERSAL-${Variable.nextFreeVariableID() }__",
                sort = f.v.sort.copy(name = f.v.sort.name + ">" + c.name, elements = Seq(c), subSorts = Nil)
              )
    }
    val g = join(And(vs map { v => f.formula update ExchangeVariable(f.v, v) }), rest)
    val replacement = gt.copy(
                               precondition = if (isPrec) g else gt.precondition,
                               effect = if (isPrec) gt.effect else g,
                               parameters = gt.parameters ++ vs
                             )
    Simplification(gt, replacement, Nil, Nil)
  }

  private def simplification(gt: GeneralTask): Either[ReducedTask, Simplification] = {
    val prec = eitherSimpleOrComplexConjunction(gt.precondition)
    lazy val eff = eitherSimpleOrComplexConjunction(gt.effect)

    if (prec.isRight) {
      val (lits, nonLits) = partitionByComplexity(prec.right.get.conjuncts)
      val rest = And(nonLits.tail ++ lits) // tail ?
      Right(nonLits.head match {
              case And(fs)        => Simplification(gt, gt.copy(precondition = And(nonLits.tail ++ fs ++ lits)), Nil, Nil)
              case f: Or[Formula] => disjunctionSimplification(gt, f, rest)
              case f: Exists      => existentialSimplification(gt, f, rest)
              case f: Forall      => universalSimplification(gt, f, rest, isPrec = true)
              case f              => throw new RuntimeException(s"Expected And|Or|Exists|Forall in complex precondition but found $f")
            })
    } else if (eff.isRight) {
      val (lits, nonLits) = partitionByComplexity(eff.right.get.conjuncts)
      val rest = And(nonLits.tail ++ lits)
      Right(nonLits.head match {
              case And(fs)   => Simplification(gt, gt.copy(effect = And(nonLits.tail ++ fs ++ lits)), Nil, Nil)
              case f: When   => conditionalSimplification(gt, f, rest)
              case f: Forall => universalSimplification(gt, f, rest, isPrec = false)
              case f         => throw new RuntimeException(s"Expected And|When|Forall in complex effect but found $f")
            })
    } else {
      Left(ReducedTask(
                        gt.name,
                        gt.isPrimitive,
                        gt.parameters,
                        gt.artificialParametersRepresentingConstants,
                        gt.parameterConstraints,
                        prec.left.get,
                        eff.left.get))
    }
  }

  private def filterLiterals(fs: Seq[Formula]): Seq[Literal] = {
    fs flatMap { _ match {case l@Literal(_, _, _) => Seq(l); case _ => Nil} }
  }

  private def filterNonLiterals(fs: Seq[Formula]): Seq[Formula] = {
    fs flatMap { _ match {case Literal(_, _, _) => Nil; case f => Seq(f)} }
  }

  private def partitionByComplexity(fs: Seq[Formula]): (Seq[Literal], Seq[Formula]) = {
    (filterLiterals(fs), filterNonLiterals(fs))
  }

  private def eitherSimpleOrComplexConjunction(f: Formula): Either[And[Literal], And[Formula]] = {
    f match {
      case l: Literal => Left(And(Seq(l)))
      case g@And(gs)  =>
        val literals: Seq[Literal] = filterLiterals(gs)
        if (literals.length == gs.length) Left(And(literals)) else Right(g)
      case _          => Right(And(Seq(f)))
    }
  }

  private def join(f: Formula, g: Formula): And[Formula] = {
    (f, g) match {
      case (And(fs), And(gs)) => And(fs ++ gs)
      case (And(fs), _)       => And(fs :+ g)
      case (_, And(gs))       => And(f +: gs)
      case _                  => And(Seq(f, g))
    }
  }

  private def filterGeneralTasks(tasks: Seq[Task]): Seq[GeneralTask] = {
    tasks flatMap { case gt: GeneralTask => Seq(gt); case _ => Nil }
  }

  private def filterReducedTasks(tasks: Seq[Task]): Seq[ReducedTask] = {
    tasks flatMap { case gt: ReducedTask => Seq(gt); case _ => Nil }
  }

  private def moveNegationsInwards(f: Formula): Formula = {
    f match {
      case Not(And(gs))             => Or(gs map { g => moveNegationsInwards(Not(g)) })
      case Not(Or(gs))              => And(gs map { g => moveNegationsInwards(Not(g)) })
      case Not(Not(g))              => moveNegationsInwards(g)
      case Not(Implies(g, h))       => moveNegationsInwards(And(Seq(g, Not(h))))
      case Not(Exists(v, g))        => Forall(v, moveNegationsInwards(Not(g)))
      case Not(Forall(v, g))        => Exists(v, moveNegationsInwards(Not(g)))
      case Not(Literal(p, sgn, vs)) => Literal(p, !sgn, vs)
      case And(gs)                  => And(gs map moveNegationsInwards)
      case Or(gs)                   => Or(gs map moveNegationsInwards)
      case Implies(g, h)            => moveNegationsInwards(Or(Seq(Not(g), h)))
      case When(g, h)               => When(moveNegationsInwards(g), moveNegationsInwards(h))
      case Exists(v, g)             => Exists(v, moveNegationsInwards(g))
      case Forall(v, g)             => Forall(v, moveNegationsInwards(g))
      case Literal(_, _, _)         => f
      case _                        => throw new IllegalArgumentException("moveNegationInwards is not applicable to: " + f)
    }
  }
}
