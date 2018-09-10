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

  def getVariableName(v: Variable, variablesWithDuplicatedNames: Set[String]): String = if (variablesWithDuplicatedNames contains v.name) v.name + "_" + v.id else v.name

  def writeParameters(vars: Seq[Variable], writeTypesWithPredicates: Boolean, variablesWithDuplicatedNames: Set[String]): String = {
    val builder: StringBuilder = new StringBuilder()
    vars.zipWithIndex foreach { case (v, i) =>
      if (i != 0) builder.append(" ")
      builder.append(toHPDDLVariableName(getVariableName(v, variablesWithDuplicatedNames)) + " - " + (if (writeTypesWithPredicates) "object" else toPDDLIdentifier(v.sort.name)))
    }
    builder.toString()
  }

  def writeVariable(v: Value, csp: CSP, variablesWithDuplicatedNames: Set[String]): String = csp getRepresentative v match {
    case Constant(name) => toPDDLIdentifier(name)
    case v: Variable    => toHPDDLVariableName(getVariableName(v, variablesWithDuplicatedNames))
  }


  def writeVariableList(vs: Seq[Value], csp: CSP, variablesWithDuplicatedNames: Set[String]): String = {
    val builder: StringBuilder = new StringBuilder()
    vs foreach { v => builder.append(" " + writeVariable(v, csp, variablesWithDuplicatedNames)) }
    builder.toString()
  }

  def writeLiteralList(literals: Seq[Literal], csp: CSP, indentation: Boolean): String = {
    val builder: StringBuilder = new StringBuilder()

    literals foreach { l =>

      builder.append("\t\t" + (if (indentation) "\t" else "") + "(")
      if (l.isNegative) builder.append("not (")
      builder.append(toPDDLIdentifier(l.predicate.name))
      builder.append(writeVariableList(l.parameterVariables, csp, Set()))
      if (l.isNegative) builder.append(")")
      builder.append(")\n")
    }
    builder.toString()
  }

  def writeLiteral(literal: Literal, uf: SymbolicUnionFind, indentation: String, appendNewLine: Boolean = true, noConstantReplacement: Boolean, variablesWithIdenticalName: Set[String]):
  String = {
    val builder: StringBuilder = new StringBuilder()

    builder.append(indentation + "(")

    if (literal.isNegative) builder.append("not (")
    builder.append(toPDDLIdentifier(literal.predicate.name))
    val parameters = if (noConstantReplacement) literal.parameterVariables else literal.parameterVariables map uf.getRepresentative
    builder.append(writeVariableList(parameters, NoConstraintsCSP, variablesWithIdenticalName))

    if (literal.isNegative) builder.append(")")
    builder.append(")")
    if (appendNewLine) builder.append("\n")
    builder.toString()
  }


  def writePlan(plan: Plan, indentation: Boolean, problemMode: Boolean, noConstantReplacement: Boolean, variablesWithIdenticalName: Set[String]): String = {
    val builder: StringBuilder = new StringBuilder()
    val unionFind = SymbolicUnionFind.constructVariableUnionFind(plan)

    // sub tasks
    builder append ("\t" + (if (indentation) "\t" else "") + ":subtasks (")
    if (plan.planStepsWithoutInitGoal.nonEmpty) builder append "and"
    builder append "\n"

    val planStepToID: Map[PlanStep, Int] = plan.planStepsWithoutInitGoal.zipWithIndex.toMap
    planStepToID.toSeq sortBy { _._1.id } foreach { case (ps, tIdx) =>
      builder.append("\t" + (if (indentation) "\t" else "") + (if (problemMode) "(" else "") + " (task" + tIdx + " (" + toPDDLIdentifier(ps.schema.name))
      val taskUF = SymbolicUnionFind.constructVariableUnionFind(ps.schema)
      val arguments = ps.arguments.zip(ps.schema.parameters) filter { case (_, schemaVar) =>
        taskUF.getRepresentative(schemaVar).isInstanceOf[Variable] || noConstantReplacement // if we don't replace constants, we have to keep all arguments
      } map { case (x, _) => if (noConstantReplacement) x else unionFind.getRepresentative(x) }
      builder.append(writeVariableList(arguments, NoConstraintsCSP, variablesWithIdenticalName))
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
      case Equal(v1, v2) if getRepresentative(v1, unionFind, noConstantReplacement, variablesWithIdenticalName) !=
        getRepresentative(v2, unionFind, noConstantReplacement, variablesWithIdenticalName)                   =>
        "\t\t\t\t(= " + getRepresentative(v1, unionFind, noConstantReplacement, variablesWithIdenticalName) + " " +
          getRepresentative(v2, unionFind, noConstantReplacement, variablesWithIdenticalName) + ")"
      case NotEqual(v1, v2) if !(getRepresentative(v1, unionFind, noConstantReplacement, variablesWithIdenticalName) !=
        getRepresentative(v2, unionFind, noConstantReplacement, variablesWithIdenticalName)
        && getRepresentative(v1, unionFind, noConstantReplacement, variablesWithIdenticalName).charAt(0) != '?' &&
        getRepresentative(v2, unionFind, noConstantReplacement, variablesWithIdenticalName).charAt(0) != '?') =>
        "\t\t\t\t(not (= " + getRepresentative(v1, unionFind, noConstantReplacement, variablesWithIdenticalName) + " " +
          getRepresentative(v2, unionFind, noConstantReplacement, variablesWithIdenticalName) + "))"
      case OfSort(v, s)                                                                                       =>
        "\t\t\t\t(sortof " + getRepresentative(v, unionFind, noConstantReplacement, variablesWithIdenticalName) + " - " + toPDDLIdentifier(s.name) + ")"
      case NotOfSort(v, s)                                                                                    =>
        "\t\t\t\t(not (sortof " + getRepresentative(v, unionFind, noConstantReplacement, variablesWithIdenticalName) + " - " + toPDDLIdentifier(s.name) + "))"
    } distinct

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
        builder append writeLiteral(literal, unionFind, "", appendNewLine = false, noConstantReplacement, variablesWithIdenticalName)
        builder append (" task" + planStepToID(consumer))

        builder append ")\n"

      }
      builder.append("\t" + (if (indentation) "\t" else "") + (if (problemMode) "(" else "") + ")\n")
    }

    builder.toString()
  }

  def getRepresentative(v: Value, uf: SymbolicUnionFind, noConstantReplacement: Boolean = false, variablesWithIdenticalName: Set[String]): String = v match {
    case Constant(c)            => toPDDLIdentifier(c)
    case vari@Variable(_, _, _) =>
      if (noConstantReplacement) toHPDDLVariableName(getVariableName(vari, variablesWithIdenticalName))
      else writeVariable(uf.getRepresentative(vari), NoConstraintsCSP, variablesWithIdenticalName)
  }


  def writeFormula(builder: StringBuilder, formula: Formula, indent: String, taskUF: SymbolicUnionFind, noConstantReplacement: Boolean, variablesWithIdenticalName: Set[String]): Unit =
    formula match {
      case Identity()               => builder
      case And(conj)                =>
        if (conj.isEmpty || (conj forall { case Identity() => true; case _ => false }))
          ()
        else {
          builder.append(indent + "(and\n")
          conj foreach {
            writeFormula(builder, _, indent + "\t", taskUF, noConstantReplacement, variablesWithIdenticalName)
          }
          builder.append(indent + ")\n")
        }
      case Or(disj)                 => builder.append(indent + "(or\n")
        disj foreach {
          writeFormula(builder, _, indent + "\t", taskUF, noConstantReplacement, variablesWithIdenticalName)
        }
        builder.append(indent + ")\n")
      case l: Literal               =>
        builder.append(writeLiteral(l, taskUF, indent, true, noConstantReplacement, variablesWithIdenticalName))
      case Not(form)                => builder.append(indent + "(not\n")
        writeFormula(builder, form, indent + "\t", taskUF, noConstantReplacement, variablesWithIdenticalName)
        builder.append(indent + ")\n")
      case Implies(left, right)     => builder.append(indent + "(imply\n")
        writeFormula(builder, left, indent + "\t", taskUF, noConstantReplacement, variablesWithIdenticalName)
        writeFormula(builder, right, indent + "\t", taskUF, noConstantReplacement, variablesWithIdenticalName)
        builder.append(indent + ")\n")
      case Equivalence(left, right) =>
        writeFormula(builder, And(Implies(left, right) :: Implies(right, left) :: Nil), indent + "\t", taskUF, noConstantReplacement, variablesWithIdenticalName)
      case Exists(v, form)          => builder.append(indent + "(exists (" + writeVariable(v, NoConstraintsCSP, variablesWithIdenticalName) + " - " + toPDDLIdentifier(v.sort.name) + ")\n")
        val quantifiedUF = new SymbolicUnionFind()
        quantifiedUF.cloneFrom(taskUF)
        quantifiedUF.addVariable(v)
        writeFormula(builder, form, indent + "\t", quantifiedUF, noConstantReplacement, variablesWithIdenticalName)
        builder.append(indent + ")\n")
      case Forall(v, form)          => builder.append(indent + "(forall (" + writeVariable(v, NoConstraintsCSP, variablesWithIdenticalName) + " - " + toPDDLIdentifier(v.sort.name) + ")\n")
        val quantifiedUF = new SymbolicUnionFind()
        quantifiedUF.cloneFrom(taskUF)
        quantifiedUF.addVariable(v)
        writeFormula(builder, form, indent + "\t", quantifiedUF, noConstantReplacement, variablesWithIdenticalName)
        builder.append(indent + ")\n")
    }

  private def writeTask(builder: StringBuilder, task: Task, encodeTypesWithPredicates: Boolean, noConstantReplacement: Boolean, variablesWithIdenticalName: Set[String]): Unit = {
    val taskUF = SymbolicUnionFind.constructVariableUnionFind(task)
    val parameters = if (noConstantReplacement) task.parameters else task.parameters filter { taskUF.getRepresentative(_).isInstanceOf[Variable] }

    builder.append("\n\t(:" + (if (task.isPrimitive) "action" else "task") + " " + toPDDLIdentifier(task.name) + "\n")
    //if (task.parameters.nonEmpty) {
    builder.append("\t\t:parameters (")
    builder.append(writeParameters(parameters, encodeTypesWithPredicates, Set()))
    builder.append(")\n")
    //}
    //builder.append("\t\t:task (" + toPDDLIdentifier(task.name))
    //parameters foreach { v => builder.append(" " + toHPDDLVariableName(v.name)) }
    //builder.append(")\n")

    // preconditions
    if (!task.precondition.isEmpty || encodeTypesWithPredicates || task.parameterConstraints.nonEmpty) {
      builder.append("\t\t:precondition \n")
      if (encodeTypesWithPredicates || task.parameterConstraints.nonEmpty) builder.append("\t\t\t(and\n")
      writeFormula(builder, task.precondition, "\t\t\t" + (if (encodeTypesWithPredicates || task.parameterConstraints.nonEmpty) "\t" else ""), taskUF, noConstantReplacement,
                   variablesWithIdenticalName)

      val constraintConditions = task.parameterConstraints collect {
        case Equal(v1, v2) if getRepresentative(v1, taskUF, noConstantReplacement, variablesWithIdenticalName) !=
          getRepresentative(v2, taskUF, noConstantReplacement, variablesWithIdenticalName)                   =>
          "\t\t\t\t(= " + getRepresentative(v1, taskUF, noConstantReplacement, variablesWithIdenticalName) + " " +
            getRepresentative(v2, taskUF, noConstantReplacement, variablesWithIdenticalName) + ")"
        case NotEqual(v1, v2) if !(getRepresentative(v1, taskUF, noConstantReplacement, variablesWithIdenticalName) !=
          getRepresentative(v2, taskUF, noConstantReplacement, variablesWithIdenticalName)
          && getRepresentative(v1, taskUF, noConstantReplacement, variablesWithIdenticalName).charAt(0) != '?' &&
          getRepresentative(v2, taskUF, noConstantReplacement, variablesWithIdenticalName).charAt(0) != '?') =>
          "\t\t\t\t(not (= " + getRepresentative(v1, taskUF, noConstantReplacement, variablesWithIdenticalName) + " " +
            getRepresentative(v2, taskUF, noConstantReplacement, variablesWithIdenticalName) + "))"
        case OfSort(v, s)                                                                                    =>
          "\t\t\t\t(sortof " + getRepresentative(v, taskUF, noConstantReplacement, variablesWithIdenticalName) + " - " + toPDDLIdentifier(s.name) + ")"
        case NotOfSort(v, s)                                                                                 =>
          "\t\t\t\t(not (sortof " + getRepresentative(v, taskUF, noConstantReplacement, variablesWithIdenticalName) + " - " + toPDDLIdentifier(s.name) + "))"
      } distinct

      if (constraintConditions.nonEmpty)
        constraintConditions foreach { condition =>
          builder append condition
          builder append "\n"
        }


      if (encodeTypesWithPredicates)
        parameters foreach { case p => builder.append("\t\t\t\t(sort_" + toPDDLIdentifier(p.sort.name) + " " + toHPDDLVariableName(p.name) + ")\n") }

      if (encodeTypesWithPredicates || task.parameterConstraints.nonEmpty)
        builder.append("\t\t\t)\n")
    } else builder.append("\t\t:precondition ()\n")
    // effects
    if (!task.effect.isEmpty) {
      builder.append("\t\t:effect\n")
      writeFormula(builder, task.effect, "\t\t\t", taskUF, noConstantReplacement, variablesWithIdenticalName)
    } else builder.append("\t\t:effect ()\n")

    builder.append("\t)\n")
  }

  def writeConstants(builder: StringBuilder, constants: Seq[Constant], dom: Domain, isInDomain: Boolean, encodeSortsWithPredicates: Boolean): Unit = if (constants.nonEmpty) {
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

  def writeTypeHierarchy(builder: StringBuilder, dom: Domain, willContainEither: Boolean): Unit =
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

  def writePredicates(builder: StringBuilder, dom: Domain, willContainEither: Boolean): Unit =
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

  def writePrimitiveActions(builder : StringBuilder, dom : Domain, willContainEither: Boolean, noConstantReplacement : Boolean) : Unit =
    dom.tasks filter { _.isPrimitive } foreach { t =>
      writeTask(builder, t, encodeTypesWithPredicates = willContainEither, noConstantReplacement = noConstantReplacement,
                t.parameters.groupBy(_.name).filter(_._2.size > 1).keySet)
    }


  override def writeDomain(dom: Domain): String = writeDomain(dom, includeAllConstants = false, noConstantReplacement = true, writeEitherWithPredicates = true)

  def writeDomain(dom: Domain, includeAllConstants: Boolean, noConstantReplacement: Boolean, writeEitherWithPredicates: Boolean): String = {
    // determine whether we would have to use either types
    val willContainEither = writeEitherWithPredicates && dom.containEitherType

    // ATTENTION: we cannot use any CSP in here, since the domain may lack constants, i.e., probably any CSP will be unsolvable causing problems
    val builder: StringBuilder = new StringBuilder()

    builder.append("(define (domain " + toPDDLIdentifier(domainName) + ")\n\t(:requirements :typing " +
                     (if (dom.decompositionMethods.nonEmpty) ":hierachie " else "") +
                     (if (dom.predicates exists { p => p.name == "typeOf" }) ":typeof-predicate " else "") +
                     ")\n")

    // add all sorts
    writeTypeHierarchy(builder, dom, willContainEither)

    if (includeAllConstants) writeConstants(builder, dom.constants, dom, isInDomain = true, encodeSortsWithPredicates = willContainEither)

    // add all predicates
    writePredicates(builder, dom, willContainEither)


    // write all abstract tasks
    // add the actual primitive actions
    dom.tasks filterNot { _.isPrimitive } foreach { t =>
      writeTask(builder, t, encodeTypesWithPredicates = willContainEither, noConstantReplacement = noConstantReplacement,
                t.parameters.groupBy(_.name).filter(_._2.size > 1).keySet)
    }


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
          case v if noConstantReplacement                              => v
          case v if planUF.getRepresentative(v).isInstanceOf[Variable] => planUF getRepresentative v
          case v                                                       => taskUF getRepresentative v
        }

        val mappedVariables = mappedParameters collect { case v: Variable => v }

        //if (m.subPlan.variableConstraints.variables.nonEmpty) {
        builder.append("\t\t:parameters (")
        val methodParameters: Seq[Variable] = {
          ((if (noConstantReplacement) m.subPlan.variableConstraints.variables.toSeq else (m.subPlan.variableConstraints.variables.toSeq map planUF.getRepresentative collect {
            case v@Variable(_, _, _) => v
          })) ++ mappedVariables).distinct.sortWith({ _.name < _.name })
        }

        val variablesWithIdenticalName = methodParameters.groupBy(_.name).filter(_._2.size > 1).keySet

        builder.append(writeParameters(methodParameters, writeTypesWithPredicates = willContainEither, variablesWithIdenticalName))
        builder.append(")\n")
        //}

        builder.append("\t\t:task (" + toPDDLIdentifier(m.abstractTask.name))
        builder.append(writeVariableList(mappedParameters, NoConstraintsCSP, variablesWithIdenticalName))
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
                                              toHPDDLVariableName(getVariableName(if (noConstantReplacement) v else taskUF.getRepresentative(v).asInstanceOf[Variable],
                                                                                  variablesWithIdenticalName)) + " " + toPDDLIdentifier(c.name) + ")\n")
            }
          if (!methodPrecondition.isEmpty) writeFormula(builder, methodPrecondition, "\t\t\t", planUF, noConstantReplacement, variablesWithIdenticalName)
          builder.append("\t\t)\n")
        }

        builder.append(writePlan(m.subPlan, indentation = true, problemMode = false, noConstantReplacement, variablesWithIdenticalName))
        builder.append("\t)\n")
    }

    // add the actual primitive actions
    writePrimitiveActions(builder,dom, willContainEither,noConstantReplacement)

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

      val variablesWithIdenticalName = methodParameters.groupBy(_.name).filter(_._2.size > 1).keySet

      builder.append(writeParameters(methodParameters, writeTypesWithPredicates = willContainEither, variablesWithIdenticalName))
      builder.append(")\n")
      //}
      builder.append(writePlan(plan, indentation = true, problemMode = false, noConstantReplacement = false, variablesWithIdenticalName))
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
