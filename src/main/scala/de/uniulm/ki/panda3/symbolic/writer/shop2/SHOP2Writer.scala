package de.uniulm.ki.panda3.symbolic.writer.shop2

import de.uniulm.ki.panda3.symbolic.csp._
import de.uniulm.ki.panda3.symbolic.domain.{Domain, SimpleDecompositionMethod}
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.writer.Writer
import de.uniulm.ki.util.{ElementaryDecomposition, GraphDecomposition, ParallelDecomposition, SequentialDecomposition}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */

object SHOP2Writer extends Writer {

  private def toSHOP2Name(s: String, isVar: Boolean): String = {
    val cleared = s.replace("+", "_plus_").replace("-", "_minus_").replace("[", "_ocb_").replace("]", "_ccb_").replace(",", "_").replace(".", "_").
      replace("?", "_question_").replace(":", "_").replace("(", "_ob_").replace(")", "_cb_").replace("<", "_lt_").replace(">", "_gt_")

    if ((cleared startsWith "?") || !isVar) cleared else "?" + cleared
  }

  private def toANMLLabel(s: String): String = toSHOP2Name(s, false)

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


  def writeVarList(vars: Seq[Variable], builder: StringBuilder): Unit = {
    vars foreach { v => builder append (" " + toSHOP2Name(v.name, isVar = true)) }
  }

  def writeLiteralList(l: Seq[Literal], builder: StringBuilder): Unit = {
    l foreach { case Literal(p, _, param) =>
      builder append ("\t\t\t(" + toSHOP2Name(p.name, isVar = false))
      writeVarList(param, builder)
      builder append ")\n"
    }
  }

  def writeVarTypePrecondition(vars: Seq[Variable], builder: StringBuilder): Unit = {
    vars foreach { v =>
      if (!(d.sorts contains v.sort)) {
        assert(v.sort.elements.size == 1)

        builder append ("\t\t\t(equal " + toSHOP2Name(v.sort.elements.head.name, isVar = false) + " " + toSHOP2Name(v.name, isVar = true) + ")\n")
      } else
        builder append ("\t\t\t(" + toSHOP2Name(v.sort.name, isVar = false) + " " + toSHOP2Name(v.name, isVar = true) + ")\n")
    }
  }

  def writeVarConstraints(constr: Seq[VariableConstraint], builder: StringBuilder): Unit = {
    constr foreach {
      case Equal(vari, value: Variable)    => builder append ("\t\t\t(equal " + toSHOP2Name(vari.name, isVar = true) + " " + toSHOP2Name(value.name, isVar = true) + ")\n")
      case Equal(vari, value: Constant)    => builder append ("\t\t\t(equal " + toSHOP2Name(vari.name, isVar = true) + " " + toSHOP2Name(value.name, isVar = false) + ")\n")
      case NotEqual(vari, value: Variable) => builder append ("\t\t\t(not (equal " + toSHOP2Name(vari.name, isVar = true) + " " + toSHOP2Name(value.name, isVar = true) + "))\n")
      case NotEqual(vari, value: Constant) => builder append ("\t\t\t(not (equal " + toSHOP2Name(vari.name, isVar = true) + " " + toSHOP2Name(value.name, isVar = false) + "))\n")
      case OfSort(v, s)                    => builder append ("\t\t\t(" + toSHOP2Name(s.name, isVar = false) + " " + toSHOP2Name(v.name, isVar = true) + ")\n")
      case NotOfSort(v, s)                 => builder append ("\t\t\t(not (" + toSHOP2Name(s.name, isVar = false) + " " + toSHOP2Name(v.name, isVar = true) + "))\n")
    }
  }

  var d: Domain = null

  /**
    * Takes a domain and writes and produces a string representation thereof.
    * This will not write any constant into the domain string
    */
  override def writeDomain(dom: Domain): String = {
    d = dom
    //assert(dom.isGround)
    val builder = new StringBuilder

    builder append "(defdomain basic (\n"


    // write primitive tasks
    dom.primitiveTasks foreach { t =>
      assert(t.preconditionsAsPredicateBool forall { _._2 }) // no negative preconditions

      builder append "\t(:operator (!" + toSHOP2Name(t.name, isVar = false)
      writeVarList(t.parameters, builder)
      builder append ")\n"

      //////////////////
      // preconditions
      builder append "\t\t(\n"

      // type constraints
      writeVarTypePrecondition(t.parameters, builder)
      // CSP constraints
      writeVarConstraints(t.parameterConstraints, builder)
      // actual preconditions
      writeLiteralList(formulaToLiteralSeq(t.precondition), builder)
      builder append "\t\t)\n"


      val (addEffect, delEffect) = formulaToLiteralSeq(t.effect) partition { _.isPositive }

      ///////////////////
      // del - effect
      builder append "\t\t(\n"
      writeLiteralList(delEffect, builder)
      builder append "\t\t)\n"

      ///////////////////
      // add - effect
      builder append "\t\t(\n"
      writeLiteralList(addEffect, builder)
      builder append "\t\t)\n"

      builder append "\t)\n\n"
    }

    // write methods

    dom.decompositionMethods foreach {
      case SimpleDecompositionMethod(at, plan, _) =>
        builder append "\t(:method (" + toSHOP2Name(at.name, isVar = false)
        writeVarList(at.parameters, builder)
        builder append ")\n"

        // method-precondition, aka everything that we cannot assert ...
        builder append "\t\t(\n"
        // type constraints
        writeVarTypePrecondition(plan.variableConstraints.variables.toSeq, builder)
        // CSP constraints
        writeVarConstraints(plan.variableConstraints.constraints, builder)
        builder append "\t\t)\n"

        // assert it is totally ordered
        //assert(plan.orderingConstraints.isTotalOrder())

        builder append "\t\t(\n"
        val orderingDecompositionOption = plan.orderingConstraints.graph.decomposition
        assert(orderingDecompositionOption.isDefined)
        val orderingDecomposition = orderingDecompositionOption.get

        def writeOrder(dec: GraphDecomposition[PlanStep]): Unit = dec match {
          case ElementaryDecomposition(ps)   =>
            builder append ("\t\t\t(" + (if (ps.schema.isPrimitive) "!" else "") + toSHOP2Name(ps.schema.name, isVar = false))
            writeVarList(ps.arguments, builder)
            builder append ")\n"
          case ParallelDecomposition(decs)   =>
            builder append "\t\t(:unordered\n"
            decs foreach { elem =>
              writeOrder(elem)
            }
            builder append "\t\t)\n"
          case SequentialDecomposition(decs) =>
            builder append "\t\t(\n"
            decs foreach { elem =>
              writeOrder(elem)
            }
            builder append "\t\t)\n"
        }

        writeOrder(orderingDecomposition)

        /*.topologicalOrdering.get foreach { case PlanStep(_, schema, args) =>
          builder append ("\t\t\t(" + (if (schema.isPrimitive) "!" else "") + toSHOP2Name(schema.name, isVar = false))
          writeVarList(args, builder)
          builder append ")\n"
        }*/

        builder append "\t\t)\n"

        builder append "\t)\n\n"
    }


    // close domain definition
    builder append "))\n"

    builder.toString()
  }

  /**
    * Takes a domain and an initial plan and generates a file representation of the planning problem.
    * The domain is necessary as all constants are by default written into the problem instance
    */
  override def writeProblem(dom: Domain, plan: Plan): String = {
    //assert(dom.isGround)
    val builder = new StringBuilder

    builder append "(defproblem problem basic\n"

    // write initial state
    builder append "\t(\n"
    plan.groundedInitialState foreach { case GroundLiteral(p, true, params) =>
      builder append "\t\t(" + toSHOP2Name(p.name, isVar = false)
      params foreach { c => builder append (" " + toSHOP2Name(c.name, isVar = false)) }
      builder append ")\n"
    case _                                                                  => // do nothing
    }

    // sort membership
    builder append "\t\n"
    dom.sorts foreach { s => s.elements foreach { c => builder append ("\t\t(" + toSHOP2Name(s.name, isVar = false) + " " + toSHOP2Name(c.name, isVar = false) + ")\n") } }

    // equals membership
    builder append "\t\n"
    dom.constants foreach { c => builder append ("\t\t(equal " + toSHOP2Name(c.name, isVar = false) + " " + toSHOP2Name(c.name, isVar = false) + ")\n") }


    builder append "\t)\n"

    // write initial plan
    assert(plan.planStepsWithoutInitGoal.length == 1)
    assert(plan.planStepsWithoutInitGoal.head.arguments.isEmpty)

    builder append "\t((" + toSHOP2Name(plan.planStepsWithoutInitGoal.head.schema.name, isVar = false) + "))\n"
    builder append ")\n"

    // write type assertions
    /* dom.constants foreach {
       c =>
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
       dom.sorts foreach {
         s =>
           s.elements foreach {
             c => builder append ("[start]" + toANMLLabel(s.name) + "(" + toANMLLabel(c.name) + ") := true;\n")
           }
       }


     // set the initial state
     //val initPositivePredicates = plan.groundedInitialStateOnlyPositive map { _.predicate } toSet
     //val initNegativePredicates = dom.predicates filterNot initPositivePredicates.contains

     //val initLiterals = (initPositivePredicates map { p => Literal(p, true, Nil) }) ++
     //  (initNegativePredicates map { p => Literal(p, false, Nil) })

     plan.init.substitutedEffects foreach {
       l => writeLiteral(l, Some(plan.variableConstraints), 0, builder, "[start]", assert = true)
     }

     // set the initial state
     plan.goal.substitutedPreconditions foreach {
       l => writeLiteral(l, Some(plan.variableConstraints), 0, builder, "[end]", assert = false)
     }
 */

    // assert

    builder.toString()
  }
}
