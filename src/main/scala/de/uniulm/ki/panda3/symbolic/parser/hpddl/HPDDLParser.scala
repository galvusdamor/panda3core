// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2017 the original author or authors.
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

package de.uniulm.ki.panda3.symbolic.parser.hpddl

import java.io.{FileInputStream, File, InputStream}

import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.parser.Parser
import de.uniulm.ki.panda3.symbolic.parser.hddl.HDDLParser
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.translation.formatConverterRonToOurs

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object HPDDLParser extends Parser {

  override def parseDomainAndProblem(domainFile: InputStream, problemFile: InputStream): (Domain, Plan) = {
    // first we translate Rons format to Daniel's and then we use the HDDL parser
    val domainString = scala.io.Source.fromInputStream(domainFile).mkString.split("\n").filterNot(s => s.trim.startsWith(";")).mkString("\n")
    val problemString = scala.io.Source.fromInputStream(problemFile).mkString.split("\n").filterNot(s => s.trim.startsWith(";")).mkString("\n")

    // the translator actually writes the files, so create targets
    val translatedDomain = File.createTempFile("domain", ".hddl")
    val translatedProblem = File.createTempFile("problem", ".hddl")

    val domainReplacementInformation = formatConverterRonToOurs.processDomain(translatedDomain.getAbsolutePath, domainString)
    formatConverterRonToOurs.processProblem(translatedProblem.getAbsolutePath, problemString, domainReplacementInformation)

    val dom = scala.io.Source.fromFile(translatedDomain.getAbsolutePath).mkString
    val prob = scala.io.Source.fromFile(translatedProblem.getAbsolutePath).mkString

    /*println()
    println(prob.split("\n").zipWithIndex.map({case (l,i) => (i+1) + l}).mkString("\n"))
    println("\n\n=======")
    println(dom.split("\n").zipWithIndex.map({case (l,i) => (i+1) + l}).mkString("\n"))

    System exit 0*/


    HDDLParser.parseDomainAndProblem(new FileInputStream(translatedDomain),new FileInputStream(translatedProblem))
  }
}
