// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2018 the original author or authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

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
    val lDomain: antlrHDDLLexer = new antlrHDDLLexer(new ANTLRInputStream(domainFile))
    val lProblem: antlrHDDLLexer = new antlrHDDLLexer(new ANTLRInputStream(problemFile))
    val pDomain: antlrHDDLParser = new antlrHDDLParser(new CommonTokenStream(lDomain))
    val pProblem: antlrHDDLParser = new antlrHDDLParser(new CommonTokenStream(lProblem))
    val result = new hddlPanda3Visitor().visitInstance(pDomain.domain, pProblem.problem)

    val methodNames = result._1.decompositionMethods map {_.name}
    val duplicates = methodNames groupBy(m => m) collect {case (m,s) if s.length > 1 => m}
    if (duplicates.nonEmpty)
      System.err.println("Warning: Domain has multiple methods with the same name. Namely: " + duplicates.mkString(", ") + ". This might lead to errors ...")

    result
  }
}
