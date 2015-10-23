package de.uniulm.ki.panda3.symbolic.parser

import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.plan.Plan

/**
 * The general trait representing parsers for domain and problem files
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait Parser {
  def parseDomain(filename: String): Domain

  def parseProblem(filename: String, domain: Domain): (Domain, Plan)
}