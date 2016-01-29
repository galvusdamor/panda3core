package de.uniulm.ki.panda3

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
package object efficient {

  /**
    * Switches a constant between its negative representation and its positive. Back and forth.
    *  Normally the constants are numbered 0..k-1. In their negative representation they are numbered -1..-k.
    */
  def switchConstant(c: Int): Int = (-c) - 1

}
