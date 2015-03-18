package de.uniulm.ki.panda3.domain

import de.uniulm.ki.panda3.logic.{Literal, Predicate}

/**
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait HasExampleDomain2 extends HasExampleDomain1 {

  // predicates
  val predicate2: Predicate = Predicate("predicate2", sort1 :: Nil)


  // tasks
  val task2: Task = Task("task2", isPrimitive = true, variable5sort1 :: Nil, Nil, preconditions = Nil,
                         effects = Literal(predicate1, isPositive = false, variable5sort1 :: Nil) :: Literal(predicate2, isPositive = true, variable5sort1 :: Nil) :: Nil)
  val goal2: Task = Task("goal", isPrimitive = true, variable6sort1 :: Nil, Nil,
                         preconditions = Literal(predicate1, isPositive = true, variable6sort1 :: Nil) :: Literal(predicate2, isPositive
                           = true, variable6sort1 :: Nil) :: Nil, effects = Nil)


  val exampleDomain2: Domain = Domain(sort1 :: Nil, constant1sort1 :: constant2sort1 :: constant3sort1 :: constant4sort1 :: Nil, predicate1 :: predicate2 :: Nil, task1 :: task2 :: Nil, Nil,
                                      Nil)
}
