package de.uniulm.ki.util

import scala.collection.mutable

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait Internable[ARGS, T] {
  protected def applyTuple: ARGS => T

  private val interningCache: mutable.WeakHashMap[ARGS, T] = new mutable.WeakHashMap()

  def intern(args: ARGS): T = this.synchronized { interningCache.getOrElseUpdate(args, { applyTuple(args) }) }
}