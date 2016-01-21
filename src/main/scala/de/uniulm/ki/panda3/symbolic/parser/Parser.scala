package de.uniulm.ki.panda3.symbolic.parser

import java.io.{InputStream, File}

import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.plan.Plan

/**
  * The general trait representing parsers for domain and problem files
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait StepwiseParser {

  def parseDomain(filename: InputStream): Domain

  def parseProblem(filename: InputStream, domain: Domain): (Domain, Plan)

  val asParser: Parser = new Parser {
    override def parseDomainAndProblem(domainFile: InputStream, problemFile: InputStream): (Domain, Plan) = {
      val domain = parseDomain(domainFile)
      parseProblem(problemFile, domain)
    }
  }
}


trait Parser {

  def parseDomainAndProblem(domainFile: InputStream, problemFile: InputStream): (Domain, Plan)
}