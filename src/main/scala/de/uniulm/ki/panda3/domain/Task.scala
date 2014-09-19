package de.uniulm.ki.panda3.domain

import de.uniulm.ki.panda3.logic.{Literal, Sort}

/**
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait Task {

  val name: String
  val isPrimitive: Boolean
  val parameterTypes: Array[Sort]
  val preconditions: Array[Literal]
  val effects: Array[Literal]

}