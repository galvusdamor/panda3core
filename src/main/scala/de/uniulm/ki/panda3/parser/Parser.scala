package de.uniulm.ki.panda3.parser

import de.uniulm.ki.panda3.domain.Domain
import de.uniulm.ki.panda3.plan.Plan

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait Parser {
  def parseDomain(filename: String): Domain

  def parseProblem(filename: String, domain: Domain): (Domain, Plan)
}