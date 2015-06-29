package de.uniulm.ki.panda3

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait PrettyPrintable {

  /** returns a string by which this object may be referenced */
  def shortInfo: String

  /** returns a string that can be utilized to define the object */
  def mediumInfo: String

  /** returns a detailed information about the object */
  def longInfo: String
}
