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

package de.uniulm.ki.panda3.symbolic.writer.simplehddl

import de.uniulm.ki.panda3.symbolic.csp._
import de.uniulm.ki.panda3.symbolic.domain.{Domain, ReducedTask, SimpleDecompositionMethod}
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.OrderingConstraint
import de.uniulm.ki.panda3.symbolic.writer.Writer

import scala.collection.mutable

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object SimpleHDDLWriter extends Writer {
  /**
    * Takes a domain and writes and produces a string representation thereof.
    * This will not write any constant into the domain string
    */
  override def writeDomain(dom: Domain): String = {
    assert(false, "The domain may not be written independently from the problem!")
    ???
  }

  private def writeLiteralList(literals: Seq[Literal], predicateMap: Map[Predicate, Int], variableMap: Map[Variable, Int], builder: StringBuilder): Unit = {
    builder.append(literals.length + "\n")

    literals foreach { l =>
      builder.append(predicateMap(l.predicate)) // number of arguments is clear from context
      l.parameterVariables foreach { v => builder.append(" " + variableMap(v)) }
      builder append "\n"
    }
  }

  private def writeConstraintList(constraints: Seq[VariableConstraint], predicateMap: Map[Predicate, Int], variableMap: Map[Variable, Int], builder: StringBuilder): Unit = {

    val constraintStrings = constraints collect {
      //case Equal(v, c: Constant)     => //builder.append("=c " + variableMap(v) + " " + constantMap(c))
      case Equal(v, v2: Variable) => "= " + variableMap(v) + " " + variableMap(v2) + "\n"
      //case NotEqual(v, c: Constant)  => //builder.append("!=c " + variableMap(v) + " " + constantMap(c))
      case NotEqual(v, v2: Variable) => "!= " + variableMap(v) + " " + variableMap(v2) + "\n"
      //case OfSort(v, s)              => //builder.append("ofsort " + variableMap(v) + " " + sortMap(s))
      //case NotOfSort(v, s)           => //builder.append("notofsort " + variableMap(v) + " " + sortMap(s))
    }

    builder.append(constraintStrings.length + "\n")
    builder.append(constraintStrings.mkString(""))

  }


  var counter: Long = 0

  // compile all the restrictions into a remaining domain
  def reduceConstraints(vari: Variable, constraints: Seq[VariableConstraint]): Sort = {
    val initialElements = vari.sort.elements.toSet

    val remainingElements = constraints.foldLeft(initialElements)(
      {
        case (elems, constraint) =>
          constraint match {
            case Equal(_, c: Constant)    => if (elems contains c) Set(c) else Set()
            case NotEqual(_, c: Constant) => elems - c
            case OfSort(_, s)             => elems intersect s.elementSet
            case NotOfSort(_, s)          => elems -- s.elements
            case _                        => elems // might contain v == v or v != v
          }
      })

    counter += 1
    Sort(vari.sort.name + "_reduced_" + counter, remainingElements.toSeq, Nil)
  }


  /**
    * Takes a domain and an initial plan and generates a file representation of the planning problem.
    * The domain is necessary as all constants are by default written into the problem instance
    */
  override def writeProblem(dom: Domain, plan: Plan): String = {
    val builder = new StringBuilder

    val elementsToSortMap = new mutable.HashMap[Set[Constant], Sort]()
    dom.declaredAndUnDeclaredSorts.foreach(s => elementsToSortMap.put(s.elementSet, s))

    // find additionally needed sorts
    val tasksAndMethodsWithVariablesAndConstraints: Seq[(Any, Seq[VariableConstraint], Variable)] =
      dom.primitiveTasks.flatMap(t => t.parameters.map(v => (t, t.parameterConstraints, v))) ++
    dom.decompositionMethods.flatMap(m => m.subPlan.variableConstraints.variables.map(v => (m, m.subPlan.variableConstraints.constraints ++ m.abstractTask.parameterConstraints, v)))

    val variableReplacementMap: Map[(Any, Variable), Sort] = tasksAndMethodsWithVariablesAndConstraints map { case (t, constraints, v) =>
      val pertainingConstraints = constraints.filter(_.getVariables.contains(v))
      println(v.name + " " + pertainingConstraints.length)
      if (pertainingConstraints.isEmpty) None else {
        val sort = reduceConstraints(v, pertainingConstraints)
        // detect sort
        if (elementsToSortMap contains sort.elementSet) Some(((t, v), elementsToSortMap(sort.elementSet)))
        else {
          elementsToSortMap.put(sort.elementSet, sort)
          Some(((t, v), sort))
        }
      }
    } collect { case Some(x) => x } toMap


    // number of constants and number of sorts
    builder.append("#number_constants_number_sorts\n")
    builder.append(dom.constants.size + " " + elementsToSortMap.size + "\n")
    builder.append("#constants\n")
    val constantsToIndexMap = dom.constants.zipWithIndex.toMap
    constantsToIndexMap.toSeq.sortBy(_._2) foreach { case (c, _) => builder.append(c.name + "\n") }
    builder.append("#end_constants\n")

    builder.append("#sorts_each_with_number_of_members_and_members\n")
    val sortToIndexMap = elementsToSortMap.values.zipWithIndex.toMap
    sortToIndexMap.toSeq.sortBy(_._2) foreach { case (s, _) =>
      builder.append(s.name + " " + s.elements.length)
      s.elements foreach { c => builder.append(" " + constantsToIndexMap(c)) }
      builder.append("\n")
    }
    builder.append("#end_sorts\n")

    // predicates
    val predicateToIndexMap = dom.predicates.zipWithIndex.toMap
    builder.append("#number_of_predicates\n")
    builder.append(dom.predicates.length + "\n")
    builder.append("#predicates_each_with_number_of_arguments_and_argument_sorts\n")
    predicateToIndexMap.toSeq.sortBy(_._2) foreach { case (p, _) =>
      builder.append(p.name + " " + p.argumentSorts.length)
      p.argumentSorts foreach { s => builder.append(" " + sortToIndexMap(elementsToSortMap(s.elementSet))) }
      builder.append("\n")
    }
    builder.append("#end_predicates\n")

    /////// primitive actions and abstract tasks
    builder.append("#number_primitiv_tasks_and_number_abstract_tasks\n")
    builder.append(dom.primitiveTasks.length + " " + dom.abstractTasks.length + "\n")
    val tasksToIndexMap = dom.primitiveTasks.zipWithIndex.toMap ++ dom.abstractTasks.zipWithIndex.map({ case (t, i) => (t, i + dom.primitiveTasks.length) })

    tasksToIndexMap.toSeq.sortBy(_._2) foreach { case (t: ReducedTask, _) =>
      builder.append("#begin_task_name_costs_number_of_variables\n")
      builder.append(t.name + " 1 " + t.parameters.length + "\n")
      val variableToIndex = t.parameters.zipWithIndex.toMap
      builder.append("#sorts_of_variables\n")
      t.parameterArray.zipWithIndex foreach { case (v, i) =>
        if (i > 0) builder.append(" ")
        val sort = if (variableReplacementMap.contains((t, v))) variableReplacementMap((t, v)) else v.sort
        builder.append(sortToIndexMap(elementsToSortMap(sort.elementSet)))
      }
      if (variableToIndex.nonEmpty) builder.append("\n")
      builder.append("#end_variables\n")

      if (t.isPrimitive) {
        // can't handle negative preconditions
        assert(t.precondition.conjuncts.forall(_.isPositive))
        builder.append("#preconditions_each_predicate_and_argument_variables\n")
        writeLiteralList(t.precondition.conjuncts, predicateToIndexMap, variableToIndex, builder)
        builder.append("#add_each_predicate_and_argument_variables\n")
        writeLiteralList(t.effect.conjuncts.filter(_.isPositive), predicateToIndexMap, variableToIndex, builder)
        builder.append("#del_each_predicate_and_argument_variables\n")
        writeLiteralList(t.effect.conjuncts.filter(_.isNegative), predicateToIndexMap, variableToIndex, builder)
        builder.append("#variable_constaints_first_number_then_individual_constraints\n")
        writeConstraintList(t.parameterConstraints, predicateToIndexMap, variableToIndex, builder)
      }
      builder.append("#end_of_task\n")
    }

    builder.append("#number_of_methods\n")
    builder.append(dom.decompositionMethods.length + "\n")
    dom.decompositionMethods foreach { case m: SimpleDecompositionMethod =>
      val occuringVariables = m.subPlan.variableConstraints.variables filter { v =>
        m.abstractTask.parameters.contains(v) || m.subPlan.planStepsWithoutInitGoal.exists(_.arguments.contains(v)) ||
          m.subPlan.variableConstraints.constraints.exists({
                                                             case eq@Equal(_,_:Variable) => eq.getVariables.contains(v)
                                                             case neq@NotEqual(_,_:Variable) => neq.getVariables.contains(v)
                                                             case _ => false
                                                           }) || (variableReplacementMap.contains((m, v)) && variableReplacementMap((m, v)).elements.isEmpty)
      }

      builder.append("#begin_method_name_abstract_task_number_of_variables\n")
      builder.append(m.name + " " + tasksToIndexMap(m.abstractTask) + " " + occuringVariables.size + "\n")
      builder.append("#variable_sorts\n")

      val variableToIndex = occuringVariables.zipWithIndex.toMap
      variableToIndex.toSeq.sortBy(_._2) foreach { case (v, i) =>
        if (i > 0) builder.append(" ")
        val sort = if (variableReplacementMap.contains((m, v))) variableReplacementMap((m, v)) else v.sort
        builder.append(sortToIndexMap(elementsToSortMap(sort.elementSet)))
      }
      if (variableToIndex.nonEmpty) builder.append("\n")

      builder.append("#parameter_of_abstract_task\n")
      // parameter of abstract task
      m.abstractTask.parameterArray.zipWithIndex foreach { case (v, i) => if (i > 0) builder.append(" "); builder.append(variableToIndex(v)) }
      if (m.abstractTask.parameterArray.nonEmpty) builder.append("\n")

      // subtasks
      val psToIndexMap = m.subPlan.planStepsWithoutInitGoal.zipWithIndex.toMap
      builder.append("#number_of_subtasks\n")
      builder.append(m.subPlan.planStepsWithoutInitGoal.size + "\n")

      builder.append("#subtasks_each_with_task_id_and_parameter_variables\n")
      psToIndexMap.toSeq.sortBy(_._2) foreach { case (ps, i) =>
        builder.append(tasksToIndexMap(ps.schema))
        ps.arguments foreach { v => builder.append(" " + variableToIndex(v)) }
        builder.append("\n")
      }

      builder.append("#number_of_ordering_constraints_and_ordering\n")
      val orderingConstraints = m.subPlan.orderingConstraints.originalOrderingConstraints filterNot { _.containsAny(m.subPlan.initAndGoal: _*) }
      builder.append(orderingConstraints.length + "\n")
      orderingConstraints foreach { case OrderingConstraint(before, after) => builder.append(psToIndexMap(before) + " " + psToIndexMap(after) + "\n") }

      builder.append("#variable_constraints\n")
      writeConstraintList(m.subPlan.variableConstraints.constraints, predicateToIndexMap, variableToIndex, builder)
      builder.append("#end_of_method\n")
    }

    //builder.append("\n")

    // write the problem
    builder.append("#init_and_goal_facts\n")
    builder.append(plan.groundedInitialTask.substitutedAddEffects.length + " " + plan.groundedGoalTask.substitutedPreconditions.length + "\n")
    plan.groundedInitialTask.substitutedAddEffects foreach { case GroundLiteral(p, _, params) =>
      builder.append(predicateToIndexMap(p))
      params foreach { c => builder.append(" " + constantsToIndexMap(c)) }
      builder.append("\n")
    }
    builder.append("#end_init\n")

    plan.groundedGoalTask.substitutedPreconditions foreach { case GroundLiteral(p, _, params) =>
      builder.append(predicateToIndexMap(p))
      params foreach { c => builder.append(" " + constantsToIndexMap(c)) }
      builder.append("\n")
    }
    builder.append("#end_goal\n")

    // initial abstract task
    assert(plan.planStepsWithoutInitGoal.size == 1)
    assert(plan.planStepsWithoutInitGoal.head.schema.parameterArray.isEmpty)
    builder.append("#initial_task\n")
    builder.append(tasksToIndexMap(plan.planStepsWithoutInitGoal.head.schema) + "\n")

    builder.toString()
  }
}




















