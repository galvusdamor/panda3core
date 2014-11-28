package de.uniulm.ki.panda3.parser

import de.uniulm.ki.panda3.domain.Domain

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait Parser {

  def parseFromFile(filename: String): Domain

}