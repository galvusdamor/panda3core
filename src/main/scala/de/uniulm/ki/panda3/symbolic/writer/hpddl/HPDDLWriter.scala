package de.uniulm.ki.panda3.symbolic.writer.hpddl

import de.uniulm.ki.panda3.symbolic.csp._
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

  def toPDDLIdentifier(id: String) : String = {
    val removedSigns = id map { c => if (c == '?') c else if (c >= 'a' && c <= 'z') c else if (c >= 'A' && c <= 'Z') c else if (c >= '0' && c <= '9') c else '_' }
    if (removedSigns.charAt(0) >= '0' && removedSigns.charAt(0) <= '9') "p" + removedSigns
    else removedSigns
  }

  def toHPDDLVariableName(name: String): String = toPDDLIdentifier(if (name.startsWith("?")) name else "?" + name)


  def writeParameters(vars: Seq[Variable]): String = {
    val builder: StringBuilder = new StringBuilder()
    vars.zipWithIndex foreach { case (v, i) =>
      if (i != 0) builder.append(" ")
      builder.append(toHPDDLVariableName(v.name) + " - " + toPDDLIdentifier(v.sort.name))
    }
    builder.toString()
  }

  def writeVariable(v: Value, csp: CSP): String = csp getRepresentative v match {
    case Constant(name) => toPDDLIdentifier(name)
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
      builder.append(toPDDLIdentifier(l.predicate.name))
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
      builder.append(toPDDLIdentifier(l.predicate.name))
      val parameters = l.parameterVariables map uf.getRepresentative
      builder.append(writeVariableList(parameters, NoConstraintsCSP))

      if (l.isNegative) builder.append(")")
      builder.append(")\n")
    }
    builder.toString()
  }


  def writePlan(plan: Plan, indentation: Boolean, problemMode: Boolean): String = {
    val builder: StringBuilder = new StringBuilder()
    val unionFind = constructUnionFind(plan)


    // sub tasks
    val planStepToID: Map[PlanStep, Int] = plan.planStepWithoutInitGoal.zipWithIndex.toMap
    planStepToID foreach { case (ps, tIdx) =>
      builder.append("\t" + (if (indentation) "\t" else "") + (if (problemMode) "(" else "") + ":tasks (task" + tIdx + " (" + toPDDLIdentifier(ps.schema.name))
      val taskUF = constructUnionFind(ps)
      val arguments = ps.arguments filter {taskUF.getRepresentative(_).isInstanceOf[Variable]} map unionFind.getRepresentative
      builder.append(writeVariableList(arguments, NoConstraintsCSP))
      builder.append("))" + (if (problemMode) ")" else "") + "\n")
    }

    // add the ordering
    val order = plan.orderingConstraints.minimalOrderingConstraints() filter {!_.containsAny(plan.init, plan.goal)}
    if (order.size != 0) {
      builder.append("\t" + (if (indentation) "\t" else "") + (if (problemMode) "(" else "") + ":ordering (")
      order foreach { case oc@OrderingConstraint(before, after) =>
        if (oc != order.head) builder.append(" ")
        builder.append("(task" + planStepToID(before) + " task" + planStepToID(after) + ")")
      }
      builder.append(")" + (if (problemMode) ")" else "") + "\n")
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

  def getRepresentative(v: Value, uf: SymbolicUnionFind): String = v match {
    case Constant(c)            => toPDDLIdentifier(c)
    case vari@Variable(_, _, _) => uf.getRepresentative(vari) match {
      case Constant(c)          => toPDDLIdentifier(c)
      case Variable(_, name, _) => toHPDDLVariableName(name)
    }
  }

  override def writeDomain(dom: Domain): String = {
    // ATTENTION: we cannot use any CSP in here, since the domain may lack constants, i.e., probably any CSP will be unsolvable causing problems
    val builder: StringBuilder = new StringBuilder()

    builder.append("(define (domain " + toPDDLIdentifier(domainName) + ")\n\t(:requirements :strips)\n")

    // add all sorts
    if (dom.sorts.size != 0) {
      builder.append("\t(:types\n")
      dom.sorts foreach { s => builder.append("\t\t" + toPDDLIdentifier(s.name))
        val parentSorts = dom.sortGraph.edgeList filter {_._2 == s}
        if (parentSorts.size == 1) builder.append(" - " + toPDDLIdentifier(parentSorts.head._1.name))
        if (parentSorts.size > 1) {
          builder.append(" - (either")
          parentSorts foreach { ps => builder.append(" " + toPDDLIdentifier(ps._1.name)) }
          builder.append(")")
        }
        builder.append("\n")
      }
      builder.append("\t)\n")
    }

    // add all predicates
    if (dom.predicates.size != 0) {
      builder.append("\t(:predicates\n")

      dom.predicates foreach { p =>
        builder.append("\t\t(" + toPDDLIdentifier(p.name))
        p.argumentSorts.zipWithIndex foreach { case (as, i) => builder.append(" ?arg" + i + " - " + toPDDLIdentifier(as.name)) }
        builder.append(")\n")
      }

      builder.append("\t)\n")
    }


    // write all abstract tasks
    if (dom.tasks exists {!_.isPrimitive}) {
      builder.append("\t(:tasks\n")

      dom.tasks filter {!_.isPrimitive} foreach { at =>
        builder.append("\t\t(" + toPDDLIdentifier(at.name) + " ")
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
      val planUF = constructUnionFind(m.subPlan)
      if (m.subPlan.variableConstraints.variables.size != 0) {
        builder.append("\t\t:parameters (")
        val methodParameters: Seq[Variable] = {
          (m.subPlan.variableConstraints.variables.toSeq map planUF.getRepresentative collect { case v@Variable(_, _, _) => v }).toSet.toSeq.sortWith({_.name < _.name})
        }
        builder.append(writeParameters(methodParameters))
        builder.append(")\n")
      }
      // write down the constraints
      val constraintConditions = m.subPlan.variableConstraints.constraints collect {
        case NotEqual(v1, v2) => "(not (= " + getRepresentative(v1, planUF) + " " + getRepresentative(v2, planUF) + "))"
        case OfSort(v, s)    => "(" + toPDDLIdentifier(s.name) + " " + getRepresentative(v, planUF) + ")"
        case NotOfSort(v, s) => "(not (" + toPDDLIdentifier(s.name) + " " + getRepresentative(v, planUF) + "))"
      }

      if (constraintConditions.size != 0) {
        builder.append("\t\t:precondition (and\n")
        constraintConditions foreach { s => builder.append("\t\t\t" + s + "\n") }
        builder.append("\t\t)\n")
      }

      builder.append("\t\t:task (" + toPDDLIdentifier(m.abstractTask.name))
      val parameters = m.abstractTask.parameters filter {planUF.getRepresentative(_).isInstanceOf[Variable]} map planUF.getRepresentative
      builder.append(writeVariableList(parameters, NoConstraintsCSP))
      builder.append(")\n")

      builder.append(writePlan(m.subPlan, indentation = true, problemMode = false))

      builder.append("\t)\n")
    }

    // add the actual primitive actions
    dom.tasks filter {_.isPrimitive} foreach { p =>

      val taskUF = constructUnionFind(p)
      val parameters = p.parameters filter {taskUF.getRepresentative(_).isInstanceOf[Variable]}

      builder.append("\n\t(:action " + toPDDLIdentifier(p.name) + "\n")
      if (p.parameters.size != 0) {
        builder.append("\t\t:parameters (")
        builder.append(writeParameters(parameters))
        builder.append(")\n")
      }
      builder.append("\t\t:task (" + toPDDLIdentifier(p.name))
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
    builder.append("\t(problem " + toPDDLIdentifier(problemName) + ")\n")
    builder.append("\t(:domain  " + toPDDLIdentifier(domainName) + ")\n")


    if (dom.constants.size != 0) {
      builder.append("\t(:objects\n")
      dom.constants foreach { c =>
        builder.append("\t\t" + toPDDLIdentifier(c.name) + " - ")
        dom.getSortOfConstant(c) match {
          case Some(s) => builder.append(toPDDLIdentifier(s.name) + "\n")
          case None    => throw new IllegalArgumentException("The constant " + c + " does not have a unique sort in the given domain.")
        }
      }
      builder.append("\t)\n")
    }

    // initial state
    if (plan.init.schema.effects.size != 0) {
      builder.append("\t(:init\n")
      builder.append(writeLiteralList(plan.init.substitutedEffects filter {_.isPositive}, plan.variableConstraints, indentation = false))
      builder.append("\t)\n")
    }

    // initial task network
    builder.append(writePlan(plan, indentation = false, problemMode = true))

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