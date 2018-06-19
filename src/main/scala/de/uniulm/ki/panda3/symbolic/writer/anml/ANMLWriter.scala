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

package de.uniulm.ki.panda3.symbolic.writer.anml

import de.uniulm.ki.panda3.symbolic.csp.{CSP, Equal, NotEqual}
import de.uniulm.ki.panda3.symbolic.domain.{Domain, SimpleDecompositionMethod}
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.writer.{Writer, toPDDLIdentifier}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */

object ANMLWriter extends Writer {

  private def toANMLLabel(s: String): String = s.replace("+", "_plus_").replace("-", "_minus_").replace("[", "_ob_").replace("]", "_cb_").replace(",", "_").replace(".", "_").
    replace("?", "_question_").replace(":", "_")

  private lazy val indentMap: Map[Int, String] = Map().withDefault({ x => (Range(0, x) map { _ => "\t" }).mkString("") })

  private def writeLiteral(literal: Literal, csp: Option[CSP], indent: Int, builder: StringBuilder, when: String, assert: Boolean): Unit = literal match {
    case Literal(predicate, isPositive, _) =>
      builder append (indentMap(indent) + when + " " + toANMLLabel(predicate.name) + "(")
      val shownValues = if (csp.isDefined) literal.parameterVariables.map(csp.get.getRepresentative) else literal.parameterVariables
      builder append shownValues.map({
                                       case v: Variable => toANMLLabel(v.name + "_" + v.id)
                                       case c: Constant => toANMLLabel(c.name)
                                     }).mkString(",")
      builder append (") " + (if (assert) ":" else "=") + "= " + (if (isPositive) "true" else "false") + ";\n")
  }

  private def formulaToLiteralSeq(f: Formula): Seq[Literal] = f match {
    case And(literals) => literals map {
      case l: Literal => l
      case _          => assert(false); null
    }
    case _             => assert(false); Nil
  }

  private def writeCSP(csp: CSP, indent: Int, builder: StringBuilder): Unit = {
    // variable constraints
    csp.constraints foreach {
      case Equal(left, right: Variable)    => builder append (indentMap(indent) + toANMLLabel(left.name + "_" + left.id) + " == " + toANMLLabel(right.name + "_" + right.id) + ";\n")
      case Equal(left, right: Constant)    => builder append (indentMap(indent) + toANMLLabel(left.name + "_" + left.id) + " == " + toANMLLabel(right.name) + ";\n")
      case NotEqual(left, right: Variable) => builder append (indentMap(indent) + toANMLLabel(left.name + "_" + left.id) + " != " + toANMLLabel(right.name + "_" + right.id) + ";\n")
      case NotEqual(left, right: Constant) => builder append (indentMap(indent) + toANMLLabel(left.name + "_" + left.id) + " != " + toANMLLabel(right.name) + ";\n")
      case _                               => // TODO we should not ignore them
    }

  }

  private def writePlan(plan: Plan, dom: Domain, declaredVariables: Seq[Variable], indent: Int, builder: StringBuilder): Unit = {
    val undeclaredVariables = plan.variableConstraints.variables -- declaredVariables
    // declare variables
    undeclaredVariables map { v =>
      builder append (indentMap(indent) + "constant " +
        (if (dom.containEitherType) "__object" else if (dom.sorts.contains(v.sort)) toANMLLabel(v.sort.name) else "__object") + " " + toANMLLabel(v.name + "_" + v.id) + ";\n")
    }

    // for ad hoc sorts, use object and a value restriction
    undeclaredVariables filterNot { v => dom.sorts contains v.sort } foreach { v =>
      assert(v.sort.elements.size == 1)
      builder append (indentMap(indent) + " " + toANMLLabel(v.name + "_" + v.id) + " == " + toANMLLabel(v.sort.elements.head.name) + ";\n")
    }

    if (dom.containEitherType)
      undeclaredVariables filter { v => dom.sorts contains v.sort } map { v =>
        indentMap(indent) + "[start] " + toANMLLabel(v.sort.name) + "(" + toANMLLabel(v.name + "_" + v.id) + ") == true;\n"
      } foreach builder.append

    writeCSP(plan.variableConstraints, indent, builder)

    // only presence
    plan.planStepsWithoutInitGoal foreach { case PlanStep(id, task, args) =>
      builder append (indentMap(indent) + "[all] contains l" + id + ":" + toANMLLabel(task.name) + "(" + args.map(x => toANMLLabel(x.name + "_" + x.id)).mkString(",") + ");\n")
    }

    plan.orderingConstraints.originalOrderingConstraints foreach { case OrderingConstraint(before, after) =>
      if ((plan.planStepsWithoutInitGoal contains before) && (plan.planStepsWithoutInitGoal contains after))
        builder append (indentMap(indent) + "end(l" + before.id + ") < start(l" + after.id + ");\n")
    }
  }

  /**
    * Takes a domain and writes and produces a string representation thereof.
    * This will not write any constant into the domain string
    */
  override def writeDomain(dom: Domain): String = {
    //assert(dom.isGround)
    val builder = new StringBuilder

    // write types
    builder.append("type __object;\n")
    if (!dom.containEitherType) {
      dom.sortGraph.topologicalOrdering.get foreach { s =>
        val parentSorts = dom.sortGraph.edgeList filter { _._2 == s }
        // if it has no parent sort it will be the parent of something
        if (parentSorts.nonEmpty)
          builder.append("type " + toANMLLabel(s.name) + " < " + toANMLLabel(parentSorts.head._1.name) + ";\n")
        else
          builder.append("type " + toANMLLabel(s.name) + " < __object;\n")
      }
    } else
      dom.sorts foreach { s => builder append ("predicate " + toANMLLabel(s.name) + "(__object arg1);\n") }

    builder append "\n"

    // write predicates
    dom.predicates foreach { p =>
      builder append ("predicate " + toANMLLabel(p.name) + "(" +
        (p.argumentSorts.zipWithIndex map { case (s, i) => (if (dom.containEitherType) "__object" else toANMLLabel(s.name)) + " arg" + i }).mkString(",") + ");\n")
    }

    //dom.tasks foreach { t => builder append ("\naction " + toANMLLabel(t.name) + "() {}; ") }

    // write tasks
    //dom.tasks foreach { t =>
    dom.taskSchemaTransitionGraph.condensation.topologicalOrdering.get.flatten.reverse foreach { t =>
      builder append ("\naction " + toANMLLabel(t.name) + "(")
      builder append (t.parameters map { v =>
        assert(dom.sorts.contains(v.sort) || v.sort.elements.size == 1)
        (if (dom.containEitherType || (!dom.sorts.contains(v.sort) && v.sort.elements.size == 1)) "__object" else toANMLLabel(v.sort.name)) + " " + toANMLLabel(v.name + "_" + v.id)
      }).mkString(",")
      builder append ") {\n"
      builder append "\tmotivated;\n"
      if (t.isPrimitive) builder append "\tduration := 1;\n"
      // if we have a non-declared parameter sort
      t.parameters filter { v => !dom.sorts.contains(v.sort) && v.sort.elements.size == 1 } foreach { v =>
        builder append ("\t" + toANMLLabel(v.name + "_" + v.id) + " == " + toANMLLabel(v.sort.elements.head.name) + ";\n")
      }
      if (dom.containEitherType)
        t.parameters map { v => "\t[start] " + toANMLLabel(v.sort.name) + "(" + toANMLLabel(v.name + "_" + v.id) + ") == true;\n" } foreach builder.append

      // ensure that the FAPE parser won't creep out
      t.parameters foreach {v => builder append ("\t " + toANMLLabel(v.name + "_" + v.id) + "!= __nothing;\n")}

      if (t.isPrimitive) {
        writeCSP(t.taskCSP, 1, builder)
        // preconditions
        formulaToLiteralSeq(t.precondition) foreach { l => writeLiteral(l, None, 1, builder, "[start]", assert = false) }
        // effects
        val effects = formulaToLiteralSeq(t.effect)
        effects filter { e => e.isPositive || !effects.contains(e.negate) } foreach { l => writeLiteral(l, None, 1, builder, "[end]", assert = true) }
      } else {
        val possibleMethods = dom.methodsForAbstractTasks(t)

        possibleMethods foreach { case SimpleDecompositionMethod(_, subplan, _) =>
          builder append "\t:decomposition{\n"
          assert(subplan.variableConstraints.variables map { _.sort } forall { s => dom.sorts.contains(s) || s.elements.size == 1 })
          writePlan(subplan, dom, t.parameters, 2, builder)

          builder append "\t};\n"
        }
      }


      builder append "};\n"
    }

    builder.toString()
  }

  /**
    * Takes a domain and an initial plan and generates a file representation of the planning problem.
    * The domain is necessary as all constants are by default written into the problem instance
    */
  override def writeProblem(dom: Domain, plan: Plan): String = {
    //assert(dom.isGround)
    val builder = new StringBuilder

    // write type assertions
    dom.constants foreach { c =>
      builder.append("instance ")
      if (dom.containEitherType) builder.append("__object ")
      else dom.getSortOfConstant(c) match {
        case Some(s) => builder.append(toANMLLabel(s.name) + " ")
        case None    => throw new IllegalArgumentException("The constant " + c + " does not have a unique sort in the given domain.")
      }
      builder append (toANMLLabel(c.name) + ";\n")
    }
    // add dummy constant
    builder append "instance __object __nothing;\n"

    if (dom.containEitherType)
      dom.sorts foreach { s => s.elements foreach { c => builder append ("[start]" + toANMLLabel(s.name) + "(" + toANMLLabel(c.name) + ") := true;\n") } }


    // set the initial state
    //val initPositivePredicates = plan.groundedInitialStateOnlyPositive map { _.predicate } toSet
    //val initNegativePredicates = dom.predicates filterNot initPositivePredicates.contains

    //val initLiterals = (initPositivePredicates map { p => Literal(p, true, Nil) }) ++
    //  (initNegativePredicates map { p => Literal(p, false, Nil) })

    plan.init.substitutedEffects foreach { l => writeLiteral(l, Some(plan.variableConstraints), 0, builder, "[start]", assert = true) }

    // set the initial state
    plan.goal.substitutedPreconditions foreach { l => writeLiteral(l, Some(plan.variableConstraints), 0, builder, "[end]", assert = false) }

    // initial plan
    writePlan(plan, dom, Nil, 0, builder)

    builder.toString()
  }
}
