package de.uniulm.ki.panda3.symbolic.parser.hddl

import java.io.{InputStream, FileReader}

import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.parser.Parser
import de.uniulm.ki.panda3.symbolic.plan.Plan
import org.antlr.v4.runtime.{CommonTokenStream, ANTLRInputStream}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object HDDLParser extends Parser {

  def parseDomainAndProblem(domainFile: InputStream, problemFile: InputStream): (Domain, Plan) = {
    val lDomain: hddlLexer = new hddlLexer(new ANTLRInputStream(domainFile))
    val lProblem: hddlLexer = new hddlLexer(new ANTLRInputStream(problemFile))
    val pDomain: hddlParser = new hddlParser(new CommonTokenStream(lDomain))
    val pProblem: hddlParser = new hddlParser(new CommonTokenStream(lProblem))
    new hddlPanda3Visitor().visitInstance(pDomain.domain, pProblem.problem)
  }
}