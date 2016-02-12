package de.uniulm.ki.panda3.symbolic.csp

import de.uniulm.ki.panda3.symbolic.domain.Task
import de.uniulm.ki.panda3.symbolic.logic.{Constant, Value, Variable}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep

/**
  * Contains a mutable union-find, containing variables and constants
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// TODO: Maybe add the sizes to "increase" efficiency
class SymbolicUnionFind {

  // contains the union-find for all variables
  // entry may also point to Constant if it known that they can only have this value
  // if the entry points to itself, then this is a top element
  private var unionFind: Map[Variable, Value] = Map[Variable, Value]()

  /** returns best known unique representative for a given variable */
  def getRepresentative(v: Variable): Value = {
    assert(unionFind.contains(v))
    unionFind(v) match {
      case c: Constant      => c
      case parent: Variable =>
        if (parent == v) v
        else {
          val representative = getRepresentative(parent)
          unionFind = unionFind + (v -> representative)
          representative
        }
    }
  }

  /** Make the two arguments equal in the union-find */
  def assertEqual(v1: Variable, v2: Value): Boolean = {
    // obtains representatives
    val v1_representative: Value = getRepresentative(v1)
    val v2_representative: Value = v2 match {
      case variable: Variable => getRepresentative(variable)
      case constant: Constant => constant
    }

    if (v1_representative == v2_representative) // if they are equal we don't have anything to do
      true
    else {
      (v1_representative, v2_representative) match {
        case (const1: Constant, const2: Constant)     => false // two unequal constants can't be made equal
        case (variable: Variable, value: Value)       => unionFind = unionFind + (variable -> value); true
        case (constant: Constant, variable: Variable) => unionFind = unionFind + (variable -> constant); true
      }
    }
  }

  /** add a new variable to the union find */
  def addVariable(v: Variable): Unit = {
    unionFind = unionFind + (v -> v)
  }

  def cloneFrom(from: SymbolicUnionFind): Unit = {
    unionFind = from.unionFind
  }
}


object SymbolicUnionFind {
  def constructVariableUnionFind(task: Task): SymbolicUnionFind = {
    val uf = new SymbolicUnionFind
    task.parameters foreach uf.addVariable
    task.parameterConstraints foreach {
      case Equal(left, right) => uf.assertEqual(left, right)
      case _                  => ()
    }
    uf
  }

  def constructVariableUnionFind(plan: Plan): SymbolicUnionFind = {
    val unionFind = new SymbolicUnionFind
    plan.variableConstraints.variables foreach unionFind.addVariable
    plan.variableConstraints.constraints foreach {
      case Equal(left, right) => unionFind.assertEqual(left, right)
      case _                  => ()
    }
    unionFind
  }


  def constructVariableUnionFind(planStep: PlanStep): SymbolicUnionFind = {
    val unionFind = new SymbolicUnionFind
    planStep.arguments foreach unionFind.addVariable
    planStep.schema.parameterConstraints map { _.substitute(planStep.schemaParameterSubstitution) } foreach {
      case Equal(left, right) => unionFind.assertEqual(left, right)
      case _                  => ()
    }
    unionFind
  }
}