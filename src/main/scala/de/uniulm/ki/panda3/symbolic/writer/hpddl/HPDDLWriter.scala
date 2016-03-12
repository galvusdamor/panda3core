package de.uniulm.ki.panda3.symbolic.writer.hpddl

import de.uniulm.ki.panda3.symbolic.csp._
import de.uniulm.ki.panda3.symbolic.domain.{SHOPDecompositionMethod, Domain, Task}
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.writer.Writer

/**
 * This is a writer for the hierarchical PDDL format created by Ronald W. Alford (ronwalf@volus.net)
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class HPDDLWriter(domainName: String, problemName: String) extends Writer {

  def toPDDLIdentifier(id: String): String = {
    val removedSigns = id map { c => if (c == '?') c else if (c >= 'a' && c <= 'z') c else if (c >= 'A' && c <= 'Z') c else if (c >= '0' && c <= '9') c else '_'}
    if (removedSigns.charAt(0) >= '0' && removedSigns.charAt(0) <= '9') "p" + removedSigns else removedSigns
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


  def writeVariableList(vs: Seq[Value], csp: CSP): String = {
    val builder: StringBuilder = new StringBuilder()
    vs foreach { v => builder.append(" " + writeVariable(v, csp))}
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

  def writeLiteral(literal: Literal, uf: SymbolicUnionFind, indentation: String): String = {
    val builder: StringBuilder = new StringBuilder()

    builder.append(indentation + "(")

    if (literal.isNegative) builder.append("not (")
    builder.append(toPDDLIdentifier(literal.predicate.name))
    val parameters = literal.parameterVariables map uf.getRepresentative
    builder.append(writeVariableList(parameters, NoConstraintsCSP))

    if (literal.isNegative) builder.append(")")
    builder.append(")\n")
    builder.toString()
  }


  def writePlan(plan: Plan, indentation: Boolean, problemMode: Boolean): String = {
    val builder: StringBuilder = new StringBuilder()
    val unionFind = SymbolicUnionFind.constructVariableUnionFind(plan)


    // sub tasks
    val planStepToID: Map[PlanStep, Int] = plan.planStepWithoutInitGoal.zipWithIndex.toMap
    planStepToID.toSeq sortBy {
      _._1.id
    } foreach { case (ps, tIdx) =>
      builder.append("\t" + (if (indentation) "\t" else "") + (if (problemMode) "(" else "") + ":tasks (task" + tIdx + " (" + toPDDLIdentifier(ps.schema.name))
      val taskUF = SymbolicUnionFind.constructVariableUnionFind(ps)
      val arguments = ps.arguments filter {
        taskUF.getRepresentative(_).isInstanceOf[Variable]
      } map unionFind.getRepresentative
      builder.append(writeVariableList(arguments, NoConstraintsCSP))
      builder.append("))" + (if (problemMode) ")" else "") + "\n")
    }

    // add the ordering
    val order = plan.orderingConstraints.minimalOrderingConstraints() filter {
      !_.containsAny(plan.init, plan.goal)
    } sortBy { o => o.before.id + plan.getFirstFreePlanStepID * o.after.id}
    if (order.nonEmpty) {
      builder.append("\t" + (if (indentation) "\t" else "") + (if (problemMode) "(" else "") + ":ordering (")
      order foreach { case oc@OrderingConstraint(before, after) =>
        if (oc != order.head) builder.append(" ")
        builder.append("(task" + planStepToID(before) + " task" + planStepToID(after) + ")")
      }
      builder.append(")" + (if (problemMode) ")" else "") + "\n")
    }

    builder.toString()
  }

  def getRepresentative(v: Value, uf: SymbolicUnionFind): String = v match {
    case Constant(c) => toPDDLIdentifier(c)
    case vari@Variable(_, _, _) => uf.getRepresentative(vari) match {
      case Constant(c) => toPDDLIdentifier(c)
      case Variable(_, name, _) => toHPDDLVariableName(name)
    }
  }


  def writeFormula(builder: StringBuilder, formula: Formula, indent: String, taskUF: SymbolicUnionFind): Unit = formula match {
    case Identity() => builder
    case And(conj) => builder.append(indent + "(and\n")
      conj foreach {
        writeFormula(builder, _, indent + "\t", taskUF)
      }
      builder.append(indent + ")\n")
    case Or(disj) => builder.append(indent + "(or\n")
      disj foreach {
        writeFormula(builder, _, indent + "\t", taskUF)
      }
      builder.append(indent + ")\n")
    case l: Literal =>
      builder.append(writeLiteral(l, taskUF, indent))
    case Not(form) => builder.append(indent + "(not\n")
      writeFormula(builder, form, indent + "\t", taskUF)
      builder.append(indent + ")\n")
    case Implies(left, right) => builder.append(indent + "(imply\n")
      writeFormula(builder, left, indent + "\t", taskUF)
      writeFormula(builder, right, indent + "\t", taskUF)
      builder.append(indent + ")\n")
    case Equivalence(left, right) => writeFormula(builder, And(Implies(left, right) :: Implies(right, left) :: Nil), indent + "\t", taskUF)
    case Exists(v, form) => builder.append(indent + "(exists (" + writeVariable(v, NoConstraintsCSP) + " - " + toPDDLIdentifier(v.sort.name) + ")\n")
      val quantifiedUF = new SymbolicUnionFind()
      quantifiedUF.cloneFrom(taskUF)
      quantifiedUF.addVariable(v)
      writeFormula(builder, form, indent + "\t", quantifiedUF)
      builder.append(indent + ")\n")
    case Forall(v, form) => builder.append(indent + "(forall (" + writeVariable(v, NoConstraintsCSP) + " - " + toPDDLIdentifier(v.sort.name) + ")\n")
      val quantifiedUF = new SymbolicUnionFind()
      quantifiedUF.cloneFrom(taskUF)
      quantifiedUF.addVariable(v)
      writeFormula(builder, form, indent + "\t", quantifiedUF)
      builder.append(indent + ")\n")
  }

  override def writeDomain(dom: Domain): String = {
    // ATTENTION: we cannot use any CSP in here, since the domain may lack constants, i.e., probably any CSP will be unsolvable causing problems
    val builder: StringBuilder = new StringBuilder()

    builder.append("(define (domain " + toPDDLIdentifier(domainName) + ")\n\t(:requirements :strips)\n")

    // add all sorts
    if (dom.sorts.nonEmpty) {
      builder.append("\t(:types\n")
      dom.sorts foreach { s => builder.append("\t\t" + toPDDLIdentifier(s.name))
        val parentSorts = dom.sortGraph.edgeList filter {
          _._2 == s
        }
        if (parentSorts.size == 1) builder.append(" - " + toPDDLIdentifier(parentSorts.head._1.name))
        if (parentSorts.size > 1) {
          builder.append(" - (either")
          parentSorts foreach { ps => builder.append(" " + toPDDLIdentifier(ps._1.name))}
          builder.append(")")
        }
        builder.append("\n")
      }
      builder.append("\t)\n")
    }

    // add all predicates
    if (dom.predicates.nonEmpty) {
      builder.append("\t(:predicates\n")

      dom.predicates foreach { p =>
        builder.append("\t\t(" + toPDDLIdentifier(p.name))
        p.argumentSorts.zipWithIndex foreach { case (as, i) => builder.append(" ?arg" + i + " - " + toPDDLIdentifier(as.name))}
        builder.append(")\n")
      }

      builder.append("\t)\n")
    }


    // write all abstract tasks
    if (dom.tasks exists {
      !_.isPrimitive
    }) {
      builder.append("\t(:tasks\n")

      dom.tasks foreach { at =>
        builder.append("\t\t(" + toPDDLIdentifier(at.name) + " ")
        val taskUF = SymbolicUnionFind.constructVariableUnionFind(at)
        val parameters = at.parameters filter {
          taskUF.getRepresentative(_).isInstanceOf[Variable]
        }
        builder.append(writeParameters(parameters))
        builder.append(")\n")
      }
      builder.append("\t)\n")
    }

    // write the decomposition methods
    dom.decompositionMethods.zipWithIndex foreach { case (m, idx) =>
      builder.append("\n")
      builder.append("\t(:method method" + idx + "\n")
      val planUF = SymbolicUnionFind.constructVariableUnionFind(m.subPlan)
      if (m.subPlan.variableConstraints.variables.nonEmpty) {
        builder.append("\t\t:parameters (")
        val methodParameters: Seq[Variable] = {
          (m.subPlan.variableConstraints.variables.toSeq map planUF.getRepresentative collect { case v@Variable(_, _, _) => v}).distinct.sortWith({
            _.name < _.name
          })
        }
        builder.append(writeParameters(methodParameters))
        builder.append(")\n")
      }
      // write down the constraints
      val constraintConditions = m.subPlan.variableConstraints.constraints collect {
        case NotEqual(v1, v2) => "(not (= " + getRepresentative(v1, planUF) + " " + getRepresentative(v2, planUF) + "))"
        case OfSort(v, s) => "(" + toPDDLIdentifier(s.name) + " " + getRepresentative(v, planUF) + ")"
        case NotOfSort(v, s) => "(not (" + toPDDLIdentifier(s.name) + " " + getRepresentative(v, planUF) + "))"
      }

      val methodPrecondition = m match {
        case SHOPDecompositionMethod(_, _, f) => f
        case _ => And[Formula](Nil)
      }

      if (constraintConditions.nonEmpty || !methodPrecondition.isEmpty) {
        builder.append("\t\t:precondition (and\n")
        if (constraintConditions.nonEmpty) constraintConditions foreach { s => builder.append("\t\t\t" + s + "\n")}
        if (!methodPrecondition.isEmpty) writeFormula(builder, methodPrecondition, "\t\t\t", planUF)
        builder.append("\t\t)\n")
      }

      builder.append("\t\t:task (" + toPDDLIdentifier(m.abstractTask.name))
      val parameters = m.abstractTask.parameters filter {
        planUF.getRepresentative(_).isInstanceOf[Variable]
      } map planUF.getRepresentative
      builder.append(writeVariableList(parameters, NoConstraintsCSP))
      builder.append(")\n")

      builder.append(writePlan(m.subPlan, indentation = true, problemMode = false))

      builder.append("\t)\n")
    }

    // add the actual primitive actions
    dom.tasks filter {
      _.isPrimitive
    } foreach { p =>

      val taskUF = SymbolicUnionFind.constructVariableUnionFind(p)
      val parameters = p.parameters filter {
        taskUF.getRepresentative(_).isInstanceOf[Variable]
      }

      builder.append("\n\t(:action " + toPDDLIdentifier(p.name) + "\n")
      if (p.parameters.nonEmpty) {
        builder.append("\t\t:parameters (")
        builder.append(writeParameters(parameters))
        builder.append(")\n")
      }
      builder.append("\t\t:task (" + toPDDLIdentifier(p.name))
      parameters foreach { v => builder.append(" " + toHPDDLVariableName(v.name))}
      builder.append(")\n")

      // preconditions
      if (!p.precondition.isEmpty) {
        builder.append("\t\t:precondition \n")
        writeFormula(builder, p.precondition, "\t\t\t", taskUF)
      }
      // effects
      if (!p.effect.isEmpty) {
        builder.append("\t\t:effect\n")
        writeFormula(builder, p.effect, "\t\t\t", taskUF)
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


    if (dom.constants.nonEmpty) {
      builder.append("\t(:objects\n")
      dom.constants foreach { c =>
        builder.append("\t\t" + toPDDLIdentifier(c.name) + " - ")
        dom.getSortOfConstant(c) match {
          case Some(s) => builder.append(toPDDLIdentifier(s.name) + "\n")
          case None => throw new IllegalArgumentException("The constant " + c + " does not have a unique sort in the given domain.")
        }
      }
      builder.append("\t)\n")
    }

    // initial state
    if (!plan.init.schema.effect.isEmpty) {
      builder.append("\t(:init\n")
      builder.append(writeLiteralList(plan.init.substitutedEffects filter {
        _.isPositive
      }, plan.variableConstraints, indentation = false))
      builder.append("\t)\n")
    }

    // initial task network
    builder.append(writePlan(plan, indentation = false, problemMode = true))

    // goal state
    if (!plan.goal.schema.precondition.isEmpty) {
      builder.append("\t(:goal\n\t\t(and\n")
      builder.append(writeLiteralList(plan.goal.substitutedPreconditions, plan.variableConstraints, indentation = false))
      builder.append("\t\t)\n")
      builder.append("\t)\n")
    }

    builder.append(")")
    builder.toString()
  }

}