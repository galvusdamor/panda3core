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

package de.uniulm.ki.panda3.symbolic.writer.gtohp

import de.uniulm.ki.panda3.symbolic.csp.{Equal, NoConstraintsCSP, NotEqual, SymbolicUnionFind}
import de.uniulm.ki.panda3.symbolic.domain.{Domain, SHOPDecompositionMethod}
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.writer.{Writer, toPDDLIdentifier}
import de.uniulm.ki.panda3.symbolic.writer.hddl.HDDLWriter
import de.uniulm.ki.panda3.symbolic.writer.hddl.HDDLWriter.toHPDDLVariableName

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class GTOHPWriter(domainName: String, problemName: String) extends Writer {
  private val hddlWriter = HDDLWriter(domainName, problemName)

  def toGTOHPIdentifier(string: String): String = toPDDLIdentifier(string)

  //.replaceAll("_","underscore").replaceAll("-","minus")
  def toGTOHPVariableName(string: String): String = toHPDDLVariableName(toGTOHPIdentifier(string))

  override def writeDomain(dom: Domain): String = {
    assert(dom.isTotallyOrdered)
    val builder: StringBuilder = new StringBuilder()
    val willContainEither = dom.containEitherType

    builder.append("(define (domain " + toGTOHPIdentifier(domainName) + ")\n\t(:requirements :strips :typing :htn :negative-preconditions :equality)\n")

    // add all sorts
    hddlWriter.writeTypeHierarchy(builder, dom, willContainEither = false)

    hddlWriter.writeConstants(builder, dom.constants, dom, isInDomain = true, encodeSortsWithPredicates = false)

    // add all predicates
    hddlWriter.writePredicates(builder, dom, willContainEither = false)

    // write primitive actions
    hddlWriter.writePrimitiveActions(builder, dom, willContainEither = false, noConstantReplacement = true)

    // write methods, these will automatically also define abstract tasks
    dom.decompositionMethods.zipWithIndex foreach {
      case (m, idx) =>
        builder.append("\n")
        builder.append("\t(:method " + toGTOHPIdentifier(m.abstractTask.name) + "\n")
        val planUF = SymbolicUnionFind.constructVariableUnionFind(m.subPlan)
        val abstractTaskParameters = m.abstractTask.parameters

        def getUnionValue(v: Variable): Value = {
          // we can't throw parameters of abstract tasks away
          abstractTaskParameters find { atp => planUF.getRepresentative(atp) == planUF.getRepresentative(v) } match {
            case Some(atp) => atp
            case None      => planUF.getRepresentative(v)
          }
        }

        val variablesWithIdenticalName = abstractTaskParameters.groupBy(_.name).filter(_._2.size > 1).keySet

        def getUnionValueString(v: Variable): String = getUnionValue(v) match {
          case vv: Variable => toGTOHPVariableName(hddlWriter.getVariableName(vv, variablesWithIdenticalName))
          case c: Constant  => toGTOHPIdentifier(c.name)
        }


        //if (m.subPlan.variableConstraints.variables.nonEmpty) {
        builder.append("\t\t:parameters (")
        builder.append(hddlWriter.writeParameters(abstractTaskParameters, writeTypesWithPredicates = false, variablesWithIdenticalName))
        builder.append(")\n")

        val taskSequence = m.subPlan.orderingConstraints.graph.topologicalOrdering.get
        builder.append("\t\t:expansion (\n")
        taskSequence foreach { ps =>
          builder.append("\t\t\t(tag t" + (ps.id - 1) + " (" + toGTOHPIdentifier(ps.schema.name))
          ps.arguments foreach { v => builder.append(" " + getUnionValueString(v)) }

          builder.append("))\n")
        }
        builder.append("\t\t)\n")

        val methodPrecondition = m match {
          case SHOPDecompositionMethod(_, _, f, _, _) => f
          case _                                      => And[Formula](Nil)
        }

        val neededVariableConstraints = abstractTaskParameters map { v => (v, planUF.getRepresentative(v)) } collect { case (v, c: Constant) => (v, c) }


        builder.append("\t\t:constraints (and\n")
        builder.append("\t\t\t(before (and\n")
        // method precondition
        hddlWriter.writeFormula(builder, methodPrecondition, "\t\t\t", planUF, noConstantReplacement = true, variablesWithIdenticalName)

        // constraints mapping abstract task parameters to inner parameters
        neededVariableConstraints foreach {
          case (v, c) => builder.append("\t\t\t(= " + toGTOHPVariableName((hddlWriter.getVariableName(v, variablesWithIdenticalName))) + " " + toGTOHPIdentifier(c.name) + ")\n")
        }


        m.subPlan.variableConstraints.constraints.filter({ case _: Equal => true; case _: NotEqual => true; case _ => false }) foreach {
          case Equal(l, c: Constant)                                         => builder.append("\t\t\t(= " + getUnionValueString(l) + " " + toGTOHPIdentifier(c.name) + ")\n")
          case Equal(l, r: Variable) if getUnionValue(l) != getUnionValue(r) => builder.append("\t\t\t(= " + getUnionValueString(l) + " " + getUnionValueString(r) + ")\n")

          case Equal(l, r: Variable) if getUnionValue(l) == getUnionValue(r) => // nothing to do
          //case NotEqual(l, c: Constant)                                               =>
          //  builder.append("\t\t\t(not (= " + toGTOHPVariableName(hddlWriter.getVariableName(getUnionVariable(l), variablesWithIdenticalName)) + " " + toGTOHPIdentifier(c.name) + "))\n")
          case NotEqual(l, r: Variable) => builder.append("\t\t\t(not (= " + getUnionValueString(l) + " " + getUnionValueString(r) + "))\n")
          case _                        =>
        }
        builder.append("\t\t\t) t" + (taskSequence.head.id - 1) + ")\n")

        builder.append("\t\t)\n")

        builder.append("\t)\n")
    }

    builder.append(")")

    builder.toString()
  }


  override def writeProblem(dom: Domain, plan: Plan): String = {
    val builder: StringBuilder = new StringBuilder()
    builder.append("(define\n")
    builder.append("\t(problem " + toGTOHPIdentifier(problemName) + ")\n")
    builder.append("\t(:domain " + toGTOHPIdentifier(domainName) + ")\n")
    builder.append("\t(:requirements :strips :typing :htn :negative-preconditions :equality)\n")

    // initial state
    if (!plan.init.schema.effect.isEmpty) {
      builder.append("\t(:init\n")
      builder.append(hddlWriter.writeLiteralList(plan.init.substitutedEffects filter { _.isPositive }, plan.variableConstraints, indentation = false))
      builder.append("\t)\n")
    }

    builder.append("\t(:goal\n")

    val planUF = SymbolicUnionFind.constructVariableUnionFind(plan)
    val methodParameters: Seq[Variable] =
      (plan.variableConstraints.variables.toSeq map planUF.getRepresentative collect {
        case v@Variable(_, _, _) => v
      }).distinct.sortWith({ _.name < _.name })
    val variablesWithIdenticalName = methodParameters.groupBy(_.name).filter(_._2.size > 1).keySet

    val taskSequence = plan.orderingConstraints.graph.topologicalOrdering.get

    builder append "\t\t:tasks (\n"
    taskSequence foreach { ps =>
      builder.append("\t\t\t(tag t" + (ps.id - 1) + " (" + toGTOHPIdentifier(ps.schema.name))
      ps.arguments map planUF.getRepresentative foreach { case c: Constant => builder.append(" " + toGTOHPIdentifier(c.name)) }
      builder.append("))\n")
    }
    builder append "\t\t)\n"


    // goal state
    builder.append("\t\t:constraints(and (after (and\n")
    if (!plan.goal.schema.precondition.isEmpty) {
      builder.append(hddlWriter.writeLiteralList(plan.goal.substitutedPreconditions, plan.variableConstraints, indentation = true))
    }
    builder.append("\t\t)\n\t\tt" + (taskSequence.last.id - 1) + "))\n")

    builder.append("\t)\n")

    builder.append(")")
    builder.toString()
  }
}
