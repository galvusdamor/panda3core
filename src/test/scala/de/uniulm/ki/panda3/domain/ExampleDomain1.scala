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
  val sort1: Sort = Sort("sort 1", constant1sort1 +: Vector())

  // predicates
  val predicate1: Predicate = Predicate("predicate1", sort1 :: Nil)

  // variables
  val variable1sort1: Variable = Variable("variable_1_sort1", sort1)
  val variable1sort2: Variable = Variable("variable_2_sort1", sort1)
  val variable1sort3: Variable = Variable("variable_3_sort1", sort1)

  // tasks
  val task1: Task = Task("task1", isPrimitive = true, variable1sort1 :: Nil, Literal(predicate1, isPositive = true, variable1sort1 :: Nil) :: Nil,
                         Literal(predicate1, isPositive = false, variable1sort1 :: Nil) :: Nil)


  val exampleDomain1: Domain = Domain(sort1 :: Nil, constant1sort1 :: constant2sort1 :: constant3sort1 :: constant4sort1 :: Nil, predicate1 :: Nil, task1 :: Nil, Nil, Nil)
}
