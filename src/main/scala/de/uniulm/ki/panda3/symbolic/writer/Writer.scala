package de.uniulm.ki.panda3.symbolic.writer

import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.plan.Plan

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait Writer {
  /**
   * Takes a domain and writes and produces a string representation thereof.
   * This will not write any constant into the domain string
   */
  def writeDomain(dom: Domain): String

  /**
   * Takes a domain and an initial plan and generates a file representation of the planning problem.
   * The domain is necessary as all constants are by default written into the problem instance
   */
  def writeProblem(dom: Domain, plan: Plan): String
}