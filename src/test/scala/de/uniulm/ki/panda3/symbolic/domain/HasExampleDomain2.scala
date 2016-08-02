package de.uniulm.ki.panda3.symbolic.domain

import de.uniulm.ki.panda3.symbolic.logic.{And, Literal, Predicate}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off magic.number
trait HasExampleDomain2 extends HasExampleDomain1 {

  // predicates
  val predicate2: Predicate = Predicate("predicate2", sort1 :: Nil)


  // tasks
  val task2: ReducedTask = ReducedTask("task2", isPrimitive = true, variableSort1(5) :: Nil, Nil, Nil,precondition = And[Literal](Nil), effect = And[Literal](
    Literal(predicate1, isPositive = false, variableSort1(5) :: Nil) :: Literal(predicate2, isPositive = true, variableSort1(5) :: Nil) :: Nil))
  val goal2: ReducedTask = ReducedTask("goal", isPrimitive = true, variableSort1(6) :: Nil, Nil,Nil,
                                precondition = And[Literal](Literal(predicate1, isPositive = true, variableSort1(6) :: Nil) :: Literal(predicate2, isPositive
                                  = true, variableSort1(6) :: Nil) :: Nil), effect = And[Literal](Nil))


  val exampleDomain2: Domain = Domain(sort1 :: Nil, predicate1 :: predicate2 :: Nil, task1 :: task2 :: Nil, Nil, Nil)
}
