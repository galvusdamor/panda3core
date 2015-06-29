package de.uniulm.ki.panda3.symbolic.domain

import de.uniulm.ki.panda3.symbolic.logic._

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait HasExampleDomain1 {

  // sorts
  val sort1: Sort = Sort("sort 1", constantSort1(1) +: constantSort1(2) +: constantSort1(3) +: constantSort1(4) +: Vector(), Nil)
  // predicates
  val predicate1: Predicate = Predicate("predicate1", sort1 :: Nil)
  // tasks
  val task1         : Task   = Task("task1", isPrimitive = true, variableSort1(2) :: Nil, Nil, preconditions = Literal(predicate1, isPositive = false, variableSort1(2) :: Nil) :: Nil,
                                    effects = Literal(predicate1, isPositive = true, variableSort1(2) :: Nil) :: Nil)
  val init          : Task   = Task("init", isPrimitive = true, variableSort1(3) :: Nil, Nil, preconditions = Nil,
                                    effects = Literal(predicate1, isPositive = false, variableSort1(3) :: Nil) :: Nil)
  val goal1         : Task   = Task("goal", isPrimitive = true, variableSort1(4) :: Nil, Nil, preconditions = Literal(predicate1, isPositive = true, variableSort1(4) :: Nil) :: Nil,
                                    effects = Nil)
  ////////////////////////////
  // the actual domain
  ////////////////////////////
  val exampleDomain1: Domain = Domain(sort1 :: Nil, predicate1 :: Nil, task1 :: Nil, Nil, Nil)

  // constants
  def constantSort1(i: Int): Constant = Constant("constant_" + i + "_sort_1")


  ////////////////////////////
  // instance
  ///////////////////////////

  // variables
  def variableSort1(i: Int): Variable = Variable(i, "variable_" + i + "_sort1", sort1)

  // variables
  def instance_variableSort1(i: Int): Variable = Variable(i, "instance_variable_" + i + "_sort1", sort1)
}
