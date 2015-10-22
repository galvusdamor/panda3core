package de.uniulm.ki.panda3.symbolic.writer.hpddl

import de.uniulm.ki.panda3.symbolic.compiler.ClosedWorldAssumption
import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.logic.{Literal, Variable}
import de.uniulm.ki.panda3.symbolic.parser.xml.XMLParser
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.writer.Writer

/**
 * This is a writer for the hierarchical PDDL format created by Ronald W. Alford (ronwalf@volus.net)
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
object HPDDLWriter extends Writer {

  def toHPDDLVariableName(name: String): String = if (name.startsWith("?")) name else "?" + name


  def writeParameters(parameters: Seq[Variable]): String = {
    val builder: StringBuilder = new StringBuilder()
    builder.append("\t\t:parameters (")
    parameters foreach { v => builder.append(toHPDDLVariableName(v.name + " - " + v.sort.name + " ")) }
    builder.append(")\n")
    builder.toString()
  }

  def writeLiteralList(literals: Seq[Literal]): String = {
    val builder: StringBuilder = new StringBuilder()

    literals foreach { l =>
      builder.append("\t\t\t(")
      if (l.isNegative) builder.append("not (")
      builder.append(l.predicate.name)
      l.parameterVariables foreach { v => builder.append(" " + toHPDDLVariableName(v.name)) }
      if (l.isNegative) builder.append(")")
      builder.append(")\n")
    }


    builder.toString()
  }


  override def writeDomain(dom: Domain): String = {
    val builder: StringBuilder = new StringBuilder()

    builder.append("(define (domain basic)\n\t(:requirements :strips)\n")

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
        builder.append("\t\t(" + at.name)
        at.parameters foreach { v => builder.append(" " + toHPDDLVariableName(v.name) + " - " + v.sort.name) }
        builder.append(")\n")
      }
      builder.append("\t)\n")
    }

    // write the decomposition methods
    dom.decompositionMethods.zipWithIndex foreach { case (m, idx) =>
      builder.append("\n")
      builder.append("\t(:method method" + idx + "\n")
      if (m.subPlan.variableConstraints.variables.size != 0) builder.append(writeParameters(m.subPlan.variableConstraints.variables.toSeq))
      builder.append("\t\t:task (" + m.abstractTask.name)
      m.abstractTask.parameters foreach { v => builder.append(" " + toHPDDLVariableName(v.name)) }
      builder.append(")\n")

      // sub tasks
      val planStepToID: Map[PlanStep, Int] = m.subPlan.planStepWithoutInitGoal.zipWithIndex.toMap
      planStepToID foreach { case (ps, tIdx) =>
        builder.append("\t\t:tasks (task" + tIdx + " (" + ps.schema.name)
        ps.arguments foreach { v => builder.append(" " + toHPDDLVariableName(v.name)) }
        builder.append("))\n")
      }

      // add the ordering
      val order = m.subPlan.orderingConstraints.minimalOrderingConstraints() filter {!_.containsAny(m.subPlan.init, m.subPlan.goal)}
      if (order.size != 0) {
        builder.append("\t\t:ordering (")
        order foreach { case OrderingConstraint(before, after) => builder.append(" (task" + planStepToID(before) + " task" + planStepToID(after) + ")") }
        builder.append(")\n")
      }

      builder.append("\t)\n")
    }

    // add the actual primitive actions
    dom.tasks filter {_.isPrimitive} foreach { p =>
      builder.append("\n\t(:action " + p.name + "\n")
      builder.append(writeParameters(p.parameters))
      builder.append("\t\t:task (" + p.name)
      p.parameters foreach { v => builder.append(" " + toHPDDLVariableName(v.name)) }
      builder.append(")\n")

      // preconditions
      if (p.preconditions.size != 0) {
        builder.append("\t\t:precondition (and\n")
        builder.append(writeLiteralList(p.preconditions))
        builder.append("\t\t)\n")
      }
      // effects
      if (p.effects.size != 0) {
        builder.append("\t\t:effect (and\n")
        builder.append(writeLiteralList(p.effects))
        builder.append("\t\t)\n")
      }

      builder.append("\t)\n")
    }


    builder.append(")")
    // return the domain we build
    builder.toString()
  }

  override def writeProblem(dom: Domain, plan: Plan): String = ???


  def main(args: Array[String]) {
    val domAlone: Domain = XMLParser.parseDomain("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/SmartPhone-HierarchicalNoAxioms.xml")
    val domAndInitialPlan: (Domain, Plan) = XMLParser.parseProblem("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/OrganizeMeeting_VerySmall.xml", domAlone)
    val sortExpansion = domAndInitialPlan._1.expandSortHierarchy()

    val parsedDom = domAndInitialPlan._1.update(sortExpansion)
    val parsedProblem = domAndInitialPlan._2.update(sortExpansion)

    // apply the CWA
    val cwaApplied = ClosedWorldAssumption.transform(parsedDom, parsedProblem, ())


    val dom = writeDomain(cwaApplied._1)

    print(dom)
  }
}