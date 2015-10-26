package de.uniulm.ki.panda3.symbolic.writer.hpddl

import de.uniulm.ki.panda3.symbolic.csp.{CSP, Equal, NoConstraintsCSP, SymbolicUnionFind}
import de.uniulm.ki.panda3.symbolic.domain.{Domain, Task}
import de.uniulm.ki.panda3.symbolic.logic.{Constant, Literal, Value, Variable}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.writer.Writer

/**
 * This is a writer for the hierarchical PDDL format created by Ronald W. Alford (ronwalf@volus.net)
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class HPDDLWriter(domainName: String, problemName: String) extends Writer {

  def toHPDDLVariableName(name: String): String = if (name.startsWith("?")) name else "?" + name


  def writeParameters(vars: Seq[Variable]): String = {
    val builder: StringBuilder = new StringBuilder()
    vars.zipWithIndex foreach { case (v, i) =>
      if (i != 0) builder.append(" ")
      builder.append(toHPDDLVariableName(v.name + " - " + v.sort.name))
    }
    builder.toString()
  }

  def writeVariable(v: Value, csp: CSP): String = csp getRepresentative v match {
    case Constant(name)       => name
    case Variable(_, name, _) => toHPDDLVariableName(name)
  }


  def writeVariableList(vs: Seq[Value], csp: CSP) = {
    val builder: StringBuilder = new StringBuilder()
    vs foreach { v => builder.append(" " + writeVariable(v, csp)) }
    builder.toString()
  }

  def writeLiteralList(literals: Seq[Literal], csp: CSP, indentation: Boolean): String = {
    val builder: StringBuilder = new StringBuilder()

    literals foreach { l =>

      builder.append("\t\t" + (if (indentation) "\t" else "") + "(")
      if (l.isNegative) builder.append("not (")
      builder.append(l.predicate.name)
      builder.append(writeVariableList(l.parameterVariables, csp))
      if (l.isNegative) builder.append(")")
      builder.append(")\n")
    }
    builder.toString()
  }

  def writeLiteralList(literals: Seq[Literal], uf: SymbolicUnionFind, indentation: Boolean): String = {
    val builder: StringBuilder = new StringBuilder()

    literals foreach { l =>

      builder.append("\t\t" + (if (indentation) "\t" else "") + "(")
      if (l.isNegative) builder.append("not (")
      builder.append(l.predicate.name)
      val parameters = l.parameterVariables map uf.getRepresentative
      builder.append(writeVariableList(parameters, NoConstraintsCSP))

      if (l.isNegative) builder.append(")")
      builder.append(")\n")
    }
    builder.toString()
  }


  def writePlan(plan: Plan, indentation: Boolean): String = {
    val builder: StringBuilder = new StringBuilder()
    val unionFind = constructUnionFind(plan)


    // sub tasks
    val planStepToID: Map[PlanStep, Int] = plan.planStepWithoutInitGoal.zipWithIndex.toMap
    planStepToID foreach { case (ps, tIdx) =>
      builder.append("\t" + (if (indentation) "\t" else "") + ":tasks (task" + tIdx + " (" + ps.schema.name)
      val taskUF = constructUnionFind(ps)
      val arguments = ps.arguments filter {taskUF.getRepresentative(_).isInstanceOf[Variable]} map unionFind.getRepresentative
      builder.append(writeVariableList(arguments, NoConstraintsCSP))
      builder.append("))\n")
    }

    // add the ordering
    val order = plan.orderingConstraints.minimalOrderingConstraints() filter {!_.containsAny(plan.init, plan.goal)}
    if (order.size != 0) {
      builder.append("\t" + (if (indentation) "\t" else "") + ":ordering (")
      order foreach { case oc@OrderingConstraint(before, after) =>
        if (oc != order.head) builder.append(" ")
        builder.append("(task" + planStepToID(before) + " task" + planStepToID(after) + ")")
      }
      builder.append(")\n")
    }

    builder.toString()
  }

  def constructUnionFind(task: Task): SymbolicUnionFind = {
    val uf = new SymbolicUnionFind
    task.parameters foreach uf.addVariable
    task.parameterConstraints foreach {
      case Equal(left, right) => uf.assertEqual(left, right)
      case _                  => ()
    }
    uf
  }

  def constructUnionFind(plan: Plan): SymbolicUnionFind = {
    val unionFind = new SymbolicUnionFind
    plan.variableConstraints.variables foreach unionFind.addVariable
    plan.variableConstraints.constraints foreach {
      case Equal(left, right) => unionFind.assertEqual(left, right)
      case _                  => ()
    }
    unionFind
  }


  def constructUnionFind(planStep: PlanStep): SymbolicUnionFind = {
    val unionFind = new SymbolicUnionFind
    planStep.arguments foreach unionFind.addVariable
    planStep.schema.parameterConstraints map {_.substitute(planStep.schemaParameterSubstitution)} foreach {
      case Equal(left, right) => unionFind.assertEqual(left, right)
      case _                  => ()
    }
    unionFind
  }


  override def writeDomain(dom: Domain): String = {
    // ATTENTION: we cannot use any CSP in here, since the domain may lack constants, i.e., probably any CSP will be unsolvable causing problems
    val builder: StringBuilder = new StringBuilder()

    builder.append("(define (domain " + domainName + ")\n\t(:requirements :strips)\n")

    // add all sorts
    if (dom.sorts.size != 0) {
      builder.append("\t(:types")
      dom.sorts foreach { s => builder.append(" " + s.name) }
      builder.append(")\n")
    }

    // add all predicates
    if (dom.predicates.size != 0) {
      builder.append("\t(:predicates\n")

      dom.predicates foreach { p =>
        builder.append("\t\t(" + p.name)
        p.argumentSorts.zipWithIndex foreach { case (as, i) => builder.append(" ?arg" + i + " - " + as.name) }
        builder.append(")\n")
      }

      builder.append("\t)\n")
    }


    // write all abstract tasks
    if (dom.tasks exists {!_.isPrimitive}) {
      builder.append("\t(:tasks\n")

      dom.tasks filter {!_.isPrimitive} foreach { at =>
        builder.append("\t\t(" + at.name + " ")
        val taskUF = constructUnionFind(at)
        val parameters = at.parameters filter {taskUF.getRepresentative(_).isInstanceOf[Variable]}
        builder.append(writeParameters(parameters))
        builder.append(")\n")
      }
      builder.append("\t)\n")
    }

    // write the decomposition methods
    dom.decompositionMethods.zipWithIndex foreach { case (m, idx) =>
      builder.append("\n")
      builder.append("\t(:method method" + idx + "\n")
      if (m.subPlan.variableConstraints.variables.size != 0) {
        builder.append("\t\t:parameters (")
        val planUF = constructUnionFind(m.subPlan)
        val methodParameters = (m.subPlan.variableConstraints.variables.toSeq filter {planUF.getRepresentative(_).isInstanceOf[Variable]}).sortWith({_.name < _.name})
        builder.append(writeParameters(methodParameters))
        builder.append(")\n")
      }
      builder.append("\t\t:task (" + m.abstractTask.name)
      val taskUF = constructUnionFind(m.abstractTask)
      val parameters = m.abstractTask.parameters filter {taskUF.getRepresentative(_).isInstanceOf[Variable]}
      builder.append(writeVariableList(parameters, NoConstraintsCSP))
      builder.append(")\n")

      builder.append(writePlan(m.subPlan, indentation = true))

      builder.append("\t)\n")
    }

    // add the actual primitive actions
    dom.tasks filter {_.isPrimitive} foreach { p =>

      val taskUF = constructUnionFind(p)
      val parameters = p.parameters filter {taskUF.getRepresentative(_).isInstanceOf[Variable]}

      builder.append("\n\t(:action " + p.name + "\n")
      if (p.parameters.size != 0) {
        builder.append("\t\t:parameters (")
        builder.append(writeParameters(parameters))
        builder.append(")\n")
      }
      builder.append("\t\t:task (" + p.name)
      parameters foreach { v => builder.append(" " + toHPDDLVariableName(v.name)) }
      builder.append(")\n")

      // preconditions
      if (p.preconditions.size != 0) {
        builder.append("\t\t:precondition (and\n")
        builder.append(writeLiteralList(p.preconditions, taskUF, indentation = true))
        builder.append("\t\t)\n")
      }
      // effects
      if (p.effects.size != 0) {
        builder.append("\t\t:effect (and\n")
        builder.append(writeLiteralList(p.effects, taskUF, indentation = true))
        builder.append("\t\t)\n")
      }

      builder.append("\t)\n")
    }


    builder.append(")")
    // return the domain we build
    builder.toString()
  }

  override def writeProblem(dom: Domain, plan: Plan): String = {
    val builder: StringBuilder = new StringBuilder()
    builder.append("(define\n")
    builder.append("\t(problem " + problemName + ")\n")
    builder.append("\t(:domain  " + domainName + ")\n")


    if (dom.constants.size != 0) {
      builder.append("\t(:objects\n")
      dom.constants foreach { c =>
        builder.append("\t\t" + c.name + " - ")
        dom.getSortOfConstant(c) match {
          case Some(s) => builder.append(s.name + "\n")
          case None    => throw new IllegalArgumentException("The constant " + c + " does not have a unique sort in the given domain.")
        }
      }
      builder.append("\t)\n")
    }

    // initial state
    if (plan.init.schema.effects.size != 0) {
      builder.append("\t(:init\n\t\t(and\n")
      builder.append(writeLiteralList(plan.init.substitutedEffects, plan.variableConstraints, indentation = false))
      builder.append("\t\t)\n")
      builder.append("\t)\n")
    }

    // initial task network
    builder.append(writePlan(plan, indentation = false))

    // goal state
    if (plan.goal.schema.effects.size != 0) {
      builder.append("\t(:goal\n\t\t(and\n")
      builder.append(writeLiteralList(plan.goal.substitutedEffects, plan.variableConstraints, indentation = false))
      builder.append("\t\t)\n")
      builder.append("\t)\n")
    }

    builder.append(")")
    builder.toString()
  }
}