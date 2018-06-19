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

package de.uniulm.ki.panda3.symbolic.writer.hddl

import de.uniulm.ki.panda3.symbolic.csp._
import de.uniulm.ki.panda3.symbolic.domain.{Task, Domain, SHOPDecompositionMethod}
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{CausalLink, OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.writer.{Writer, _}
import de.uniulm.ki.util.Dot2PdfCompiler


/**
  * This is a writer for the hierarchical DDL format created by Daniel Höller (daniel.höller@uni-ulm.de)
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class HDDLWriter(domainName: String, problemName: String) extends Writer {

  import HDDLWriter._

  def writeParameters(vars: Seq[Variable], writeTypesWithPredicates: Boolean): String = {
    val builder: StringBuilder = new StringBuilder()
    vars.zipWithIndex foreach { case (v, i) =>
      if (i != 0) builder.append(" ")
      builder.append(toHPDDLVariableName(v.name) + " - " + (if (writeTypesWithPredicates) "object" else toPDDLIdentifier(v.sort.name)))
    }
    builder.toString()
  }

  def writeVariable(v: Value, csp: CSP): String = csp getRepresentative v match {
    case Constant(name)       => toPDDLIdentifier(name)
    case Variable(_, name, _) => toHPDDLVariableName(name)
  }


  def writeVariableList(vs: Seq[Value], csp: CSP): String = {
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

  def writeLiteral(literal: Literal, uf: SymbolicUnionFind, indentation: String, appendNewLine: Boolean = true, noConstantReplacement: Boolean): String = {
    val builder: StringBuilder = new StringBuilder()

    builder.append(indentation + "(")

    if (literal.isNegative) builder.append("not (")
    builder.append(toPDDLIdentifier(literal.predicate.name))
    val parameters = if (noConstantReplacement) literal.parameterVariables else literal.parameterVariables map uf.getRepresentative
    builder.append(writeVariableList(parameters, NoConstraintsCSP))

    if (literal.isNegative) builder.append(")")
    builder.append(")")
    if (appendNewLine) builder.append("\n")
    builder.toString()
  }


  def writePlan(plan: Plan, indentation: Boolean, problemMode: Boolean, noConstantReplacement: Boolean): String = {
    val builder: StringBuilder = new StringBuilder()
    val unionFind = SymbolicUnionFind.constructVariableUnionFind(plan)

    // sub tasks
    builder append ("\t" + (if (indentation) "\t" else "") + ":subtasks (")
    if (plan.planStepsWithoutInitGoal.nonEmpty) builder append "and"
    builder append "\n"

    val planStepToID: Map[PlanStep, Int] = plan.planStepsWithoutInitGoal.zipWithIndex.toMap
    planStepToID.toSeq sortBy {
      _._1.id
    } foreach { case (ps, tIdx) =>
      builder.append("\t" + (if (indentation) "\t" else "") + (if (problemMode) "(" else "") + " (task" + tIdx + " (" + toPDDLIdentifier(ps.schema.name))
      val taskUF = SymbolicUnionFind.constructVariableUnionFind(ps)
      val arguments = ps.arguments filter {
        taskUF.getRepresentative(_).isInstanceOf[Variable]
      } map unionFind.getRepresentative
      builder.append(writeVariableList(arguments, NoConstraintsCSP))
      builder.append("))" + (if (problemMode) ")" else "") + "\n")
    }
    builder append ("\t" + (if (indentation) "\t" else "") + ")\n")

    // add the ordering
    val order = plan.orderingConstraints.minimalOrderingConstraints() filter {
      !_.containsAny(plan.init, plan.goal)
    } sortBy { o => o.before.id + plan.getFirstFreePlanStepID * o.after.id }

    if (order.nonEmpty) {
      builder.append("\t" + (if (indentation) "\t" else "") + (if (problemMode) "(" else "") + ":ordering (and\n")
      order foreach { case oc@OrderingConstraint(before, after) =>
        builder.append("\t" + (if (indentation) "\t" else "") + (if (problemMode) "(" else "") + "\t")
        builder.append("(task" + planStepToID(before) + " < task" + planStepToID(after) + ")\n")
      }

      builder.append("\t" + (if (indentation) "\t" else "") + (if (problemMode) "(" else "") + ")\n")
    }


    // write down the constraints
    val constraintConditions = plan.variableConstraints.constraints collect {
      case NotEqual(v1, v2) => "(not (= " + getRepresentative(v1, unionFind) + " " + getRepresentative(v2, unionFind) + "))"
      case OfSort(v, s)     => "(sortof " + getRepresentative(v, unionFind) + " - " + toPDDLIdentifier(s.name) + ")"
      case NotOfSort(v, s)  => "(not (sortof " + getRepresentative(v, unionFind) + " - " + toPDDLIdentifier(s.name) + "))"
    }

    if (constraintConditions.nonEmpty) {
      builder.append("\t" + (if (indentation) "\t" else "") + (if (problemMode) "(" else "") + ":constraints (and\n")
      constraintConditions foreach { condition =>
        builder.append("\t" + (if (indentation) "\t" else "") + (if (problemMode) "(" else "") + "\t")
        builder append condition
        builder append ((if (problemMode) ")" else "") + "\n")
      }

      builder.append("\t" + (if (indentation) "\t" else "") + (if (problemMode) "(" else "") + ")\n")
    }

    if (plan.causalLinks.nonEmpty) {
      builder.append("\t" + (if (indentation) "\t" else "") + (if (problemMode) "(" else "") + ":causallinks (and\n")
      plan.causalLinks foreach { case CausalLink(producer, consumer, literal) =>
        builder.append("\t" + (if (indentation) "\t" else "") + "\t(")
        builder append ("task" + planStepToID(producer) + " ")
        builder append writeLiteral(literal, unionFind, "", appendNewLine = false, noConstantReplacement)
        builder append (" task" + planStepToID(consumer))

        builder append ")\n"

      }
      builder.append("\t" + (if (indentation) "\t" else "") + (if (problemMode) "(" else "") + ")\n")
    }

    builder.toString()
  }

  def getRepresentative(v: Value, uf: SymbolicUnionFind): String = v match {
    case Constant(c)            => toPDDLIdentifier(c)
    case vari@Variable(_, _, _) => uf.getRepresentative(vari) match {
      case Constant(c)          => toPDDLIdentifier(c)
      case Variable(_, name, _) => toHPDDLVariableName(name)
    }
  }


  def writeFormula(builder: StringBuilder, formula: Formula, indent: String, taskUF: SymbolicUnionFind, noConstantReplacement: Boolean): Unit = formula match {
    case Identity()               => builder
    case And(conj)                =>
      if (conj.isEmpty || (conj forall { case Identity() => true; case _ => false }))
        ()
      else {
        builder.append(indent + "(and\n")
        conj foreach {
          writeFormula(builder, _, indent + "\t", taskUF, noConstantReplacement)
        }
        builder.append(indent + ")\n")
      }
    case Or(disj)                 => builder.append(indent + "(or\n")
      disj foreach {
        writeFormula(builder, _, indent + "\t", taskUF, noConstantReplacement)
      }
      builder.append(indent + ")\n")
    case l: Literal               =>
      builder.append(writeLiteral(l, taskUF, indent, noConstantReplacement, noConstantReplacement))
    case Not(form)                => builder.append(indent + "(not\n")
      writeFormula(builder, form, indent + "\t", taskUF, noConstantReplacement)
      builder.append(indent + ")\n")
    case Implies(left, right)     => builder.append(indent + "(imply\n")
      writeFormula(builder, left, indent + "\t", taskUF, noConstantReplacement)
      writeFormula(builder, right, indent + "\t", taskUF, noConstantReplacement)
      builder.append(indent + ")\n")
    case Equivalence(left, right) => writeFormula(builder, And(Implies(left, right) :: Implies(right, left) :: Nil), indent + "\t", taskUF, noConstantReplacement)
    case Exists(v, form)          => builder.append(indent + "(exists (" + writeVariable(v, NoConstraintsCSP) + " - " + toPDDLIdentifier(v.sort.name) + ")\n")
      val quantifiedUF = new SymbolicUnionFind()
      quantifiedUF.cloneFrom(taskUF)
      quantifiedUF.addVariable(v)
      writeFormula(builder, form, indent + "\t", quantifiedUF, noConstantReplacement)
      builder.append(indent + ")\n")
    case Forall(v, form)          => builder.append(indent + "(forall (" + writeVariable(v, NoConstraintsCSP) + " - " + toPDDLIdentifier(v.sort.name) + ")\n")
      val quantifiedUF = new SymbolicUnionFind()
      quantifiedUF.cloneFrom(taskUF)
      quantifiedUF.addVariable(v)
      writeFormula(builder, form, indent + "\t", quantifiedUF, noConstantReplacement)
      builder.append(indent + ")\n")
  }

  private def writeTask(builder: StringBuilder, task: Task, encodeTypesWithPredicates: Boolean, noConstantReplacement: Boolean): Unit = {
    val taskUF = SymbolicUnionFind.constructVariableUnionFind(task)
    val parameters = if (noConstantReplacement) task.parameters else (task.parameters filter { taskUF.getRepresentative(_).isInstanceOf[Variable] })

    builder.append("\n\t(:" + (if (task.isPrimitive) "action" else "task") + " " + toPDDLIdentifier(task.name) + "\n")
    //if (task.parameters.nonEmpty) {
    builder.append("\t\t:parameters (")
    builder.append(writeParameters(parameters, encodeTypesWithPredicates))
    builder.append(")\n")
    //}
    //builder.append("\t\t:task (" + toPDDLIdentifier(task.name))
    //parameters foreach { v => builder.append(" " + toHPDDLVariableName(v.name)) }
    //builder.append(")\n")

    // preconditions
    if (!task.precondition.isEmpty || encodeTypesWithPredicates) {
      builder.append("\t\t:precondition \n")
      if (encodeTypesWithPredicates) builder.append("\t\t\t(and\n")
      writeFormula(builder, task.precondition, "\t\t\t" + (if (encodeTypesWithPredicates) "\t" else ""), taskUF, noConstantReplacement)
      if (encodeTypesWithPredicates) {
        parameters foreach { case p => builder.append("\t\t\t\t(sort_" + toPDDLIdentifier(p.sort.name) + " " + toHPDDLVariableName(p.name) + ")\n") }
        builder.append("\t\t\t)\n")
      }
    } else builder.append("\t\t:precondition ()\n")
    // effects
    if (!task.effect.isEmpty) {
      builder.append("\t\t:effect\n")
      writeFormula(builder, task.effect, "\t\t\t", taskUF, noConstantReplacement)
    } else builder.append("\t\t:effect ()\n")

    builder.append("\t)\n")
  }

  private def writeConstants(builder: StringBuilder, constants: Seq[Constant], dom: Domain, isInDomain: Boolean, encodeSortsWithPredicates: Boolean): Unit = if (constants.nonEmpty) {
    builder.append("\t(:" + (if (isInDomain) "constants" else "objects") + "\n")
    constants foreach { c =>
      builder.append("\t\t" + toPDDLIdentifier(c.name) + " - ")
      if (encodeSortsWithPredicates) builder.append("object")
      else dom.getSortOfConstant(c) match {
        case Some(s) => builder.append(toPDDLIdentifier(s.name) + "\n")
        case None    => throw new IllegalArgumentException("The constant " + c + " does not have a unique sort in the given domain.")
      }
    }
    builder.append("\t)\n")
  }

  override def writeDomain(dom: Domain): String = writeDomain(dom, includeAllConstants = false, noConstantReplacement = true, writeEitherWithPredicates = true)

  def writeDomain(dom: Domain, includeAllConstants: Boolean, noConstantReplacement: Boolean, writeEitherWithPredicates: Boolean): String = {
    // determine whether we would have to use either types
    val willContainEither = writeEitherWithPredicates && dom.containEitherType

    // ATTENTION: we cannot use any CSP in here, since the domain may lack constants, i.e., probably any CSP will be unsolvable causing problems
    val builder: StringBuilder = new StringBuilder()

    builder.append("(define (domain " + toPDDLIdentifier(domainName) + ")\n\t(:requirements :typing " + (if (dom.decompositionMethods.nonEmpty) ":hierachie" else "") + ")\n")

    // add all sorts
    if (dom.sorts.nonEmpty) {
      builder.append("\t(:types\n")
      if (willContainEither) builder.append("\t\tobject\n")
      else {
        val (regularSorts, isolatedSorts) = dom.sorts partition { s => dom.sortGraph.degrees(s) match {case (a, b) => a != 0 || b != 0} }


        regularSorts foreach { s =>
          val parentSorts = dom.sortGraph.edgeList filter { _._2 == s }
          // if it has no parent sort it will be the parent of something
          if (parentSorts.nonEmpty) {
            if (parentSorts.size == 1) builder.append("\t\t" + toPDDLIdentifier(s.name) + " - " + toPDDLIdentifier(parentSorts.head._1.name) + "\n")
            if (parentSorts.size > 1) {
              parentSorts foreach { ps =>
                builder.append("\t\t" + toPDDLIdentifier(s.name) + " - " + toPDDLIdentifier(ps._1.name) + "\n")
              }
            }
          }
        }
        // write isolated sorts at the end
        isolatedSorts foreach { s => builder.append("\t\t" + toPDDLIdentifier(s.name) + "\n") }
      }
      builder.append("\t)\n")
    }

    if (includeAllConstants) writeConstants(builder, dom.constants, dom, isInDomain = true, encodeSortsWithPredicates = willContainEither)

    // add all predicates
    if (dom.predicates.nonEmpty) {
      builder.append("\t(:predicates\n")

      dom.predicates foreach {
        p =>
          builder.append("\t\t(" + toPDDLIdentifier(p.name))
          p.argumentSorts.zipWithIndex foreach {
            case (as, i) => builder.append(" ?arg" + i + " - " + (if (willContainEither) "object" else toPDDLIdentifier(as.name)))
          }
          builder.append(")\n")
      }

      if (willContainEither) dom.sorts foreach { s => builder.append("\t\t(sort_" + toPDDLIdentifier(s.name) + " ?arg0 - object)\n") }

      builder.append("\t)\n")
    }


    // write all abstract tasks
    // add the actual primitive actions
    dom.tasks filterNot { _.isPrimitive } foreach { writeTask(builder, _, encodeTypesWithPredicates = willContainEither, noConstantReplacement = noConstantReplacement) }


    // write the decomposition methods
    dom.decompositionMethods.zipWithIndex foreach {
      case (m, idx) =>
        builder.append("\n")
        builder.append("\t(:method " + toPDDLIdentifier(m.name) + "\n")
        val planUF = SymbolicUnionFind.constructVariableUnionFind(m.subPlan)

        // we can't throw parameters of abstract tasks away
        val taskUF = SymbolicUnionFind.constructVariableUnionFind(m.abstractTask)
        val abstractTaskParameters = if (noConstantReplacement) m.abstractTask.parameters else (m.abstractTask.parameters filter { taskUF.getRepresentative(_).isInstanceOf[Variable] })
        val mappedParameters = abstractTaskParameters map {
          case v if planUF.getRepresentative(v).isInstanceOf[Variable] => planUF getRepresentative v
          case v                                                       => taskUF getRepresentative v
        }

        val mappedVariables = mappedParameters collect { case v: Variable => v }

        //if (m.subPlan.variableConstraints.variables.nonEmpty) {
        builder.append("\t\t:parameters (")
        val methodParameters: Seq[Variable] = {
          ((m.subPlan.variableConstraints.variables.toSeq map planUF.getRepresentative collect {
            case v@Variable(_, _, _) => v
          }) ++ mappedVariables).distinct.sortWith({ _.name < _.name })
        }

        builder.append(writeParameters(methodParameters, writeTypesWithPredicates = willContainEither))
        builder.append(")\n")
        //}

        builder.append("\t\t:task (" + toPDDLIdentifier(m.abstractTask.name))
        builder.append(writeVariableList(mappedParameters, NoConstraintsCSP))
        builder.append(")\n")

        // TODO method effects
        val methodPrecondition = m match {
          case SHOPDecompositionMethod(_, _, f, _, _) => f
          case _                                      => And[Formula](Nil)
        }

        val neededVariableConstraints = abstractTaskParameters map { v => (v, planUF.getRepresentative(v)) } collect { case (v, c: Constant) => (v, c) }

        if (!methodPrecondition.isEmpty || neededVariableConstraints.nonEmpty) {
          builder.append("\t\t:precondition (and\n")
          if (neededVariableConstraints.nonEmpty)
            neededVariableConstraints foreach {
              case (v, c) => builder.append("\t\t\t(= " +
                                              (if (noConstantReplacement) toHPDDLVariableName(v.name) else toHPDDLVariableName(taskUF.getRepresentative(v).asInstanceOf[Variable].name)) + " " +
                                                toPDDLIdentifier(c.name) + ")\n")
            }
          if (!methodPrecondition.isEmpty) writeFormula(builder, methodPrecondition, "\t\t\t", planUF, noConstantReplacement)
          builder.append("\t\t)\n")
        }

        builder.append(writePlan(m.subPlan, indentation = true, problemMode = false, noConstantReplacement))
        builder.append("\t)\n")
    }

    // add the actual primitive actions
    dom.tasks filter { _.isPrimitive } foreach { writeTask(builder, _, encodeTypesWithPredicates = willContainEither, noConstantReplacement = noConstantReplacement) }


    builder.append(")")
    // return the domain we build
    builder.toString()
  }

  override def writeProblem(dom: Domain, plan: Plan): String = writeProblem(dom, plan, writeEitherWithPredicates = false)

  def writeProblem(dom: Domain, plan: Plan, writeEitherWithPredicates: Boolean): String = {
    // determine whether we would have to use either types
    val willContainEither = writeEitherWithPredicates && dom.containEitherType

    val builder: StringBuilder = new StringBuilder()
    builder.append("(define\n")
    builder.append("\t(problem " + toPDDLIdentifier(problemName) + ")\n")
    builder.append("\t(:domain  " + toPDDLIdentifier(domainName) + ")\n")

    writeConstants(builder, dom.constants, dom, isInDomain = false, encodeSortsWithPredicates = willContainEither)

    // initial task network
    if (plan.planStepsWithoutInitGoal.nonEmpty) {
      builder append "\t(:htn\n"
      //if (m.subPlan.variableConstraints.variables.nonEmpty) {
      builder.append("\t\t:parameters (")
      val planUF = SymbolicUnionFind.constructVariableUnionFind(plan)
      val methodParameters: Seq[Variable] =
        (plan.variableConstraints.variables.toSeq map planUF.getRepresentative collect {
          case v@Variable(_, _, _) => v
        }).distinct.sortWith({ _.name < _.name })

      builder.append(writeParameters(methodParameters, writeTypesWithPredicates = willContainEither))
      builder.append(")\n")
      //}
      builder.append(writePlan(plan, indentation = true, problemMode = false, noConstantReplacement = false))
      builder append "\t)\n"
    }

    // initial state
    if (!plan.init.schema.effect.isEmpty) {
      builder.append("\t(:init\n")
      builder.append(writeLiteralList(plan.init.substitutedEffects filter { _.isPositive }, plan.variableConstraints, indentation = false))
      // write sort predicates if necessary
      if (willContainEither) dom.sorts.foreach { s => s.elements foreach { c => builder.append("\t\t(sort_" + s.name + " " + c.name + ")\n") } }
      builder.append("\t)\n")
    }

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

object HDDLWriter {
  def toHPDDLVariableName(name: String): String = toPDDLIdentifier(if (name.startsWith("?")) name else "?" + name)
}
