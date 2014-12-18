package de.uniulm.ki.panda3.domain

import de.uniulm.ki.panda3.csp.Variable
import de.uniulm.ki.panda3.logic.{Constant, Literal, Predicate, Sort}

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait HasExampleDomain1 {

  // constants
  val constant1sort1: Constant = Constant("constant_1_sort_1")
  val constant2sort1: Constant = Constant("constant_2_sort_1")
  val constant3sort1: Constant = Constant("constant_3_sort_1")
  val constant4sort1: Constant = Constant("constant_4_sort_1")

  // sorts
  val sort1: Sort = Sort("sort 1", constant1sort1 +: constant2sort1 +: constant3sort1 +: constant4sort1 +: Vector())

  // predicates
  val predicate1: Predicate = Predicate("predicate1", sort1 :: Nil)

  // variables
  val variable1sort1: Variable = Variable(1, "variable_1_sort1", sort1)
  val variable2sort1: Variable = Variable(2, "variable_2_sort1", sort1)
  val variable3sort1: Variable = Variable(3, "variable_3_sort1", sort1)

  // tasks
  val task1: Task = Task("task1", isPrimitive = true, variable1sort1 :: Nil, Nil, preconditions = Literal(predicate1, isPositive = false, variable1sort1 :: Nil) :: Nil, effects = Literal
    (predicate1, isPositive = true, variable1sort1 :: Nil) :: Nil)
  val init: Task = Task("init", isPrimitive = true, variable1sort1 :: Nil, Nil, preconditions = Nil, effects = Literal(predicate1, isPositive = false, variable1sort1 :: Nil) :: Nil)
  val goal1: Task = Task("goal", isPrimitive = true, variable1sort1 :: Nil, Nil, preconditions = Literal(predicate1, isPositive = true, variable1sort1 :: Nil) :: Nil, effects = Nil)


  ////////////////////////////
  // instance
  ///////////////////////////

  // variables
  val instance_variable1sort1: Variable = Variable(1, "variable_1_sort1", sort1)
  val instance_variable1sort2: Variable = Variable(2, "variable_2_sort1", sort1)
  val instance_variable1sort3: Variable = Variable(3, "variable_3_sort1", sort1)
  val instance_variable1sort4: Variable = Variable(4, "variable_4_sort1", sort1)
  val instance_variable1sort5: Variable = Variable(5, "variable_5_sort1", sort1)


  ////////////////////////////
  // the actual domain
  ////////////////////////////
  val exampleDomain1: Domain = Domain(sort1 :: Nil, constant1sort1 :: constant2sort1 :: constant3sort1 :: constant4sort1 :: Nil, predicate1 :: Nil, task1 :: Nil, Nil, Nil)
}
