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
    val domainString = scala.io.Source.fromInputStream(domainFile).mkString
    val problemString = scala.io.Source.fromInputStream(problemFile).mkString

    // the translator actually writes the files, so create targets
    val translatedDomain = File.createTempFile("domain", ".hddl")
    val translatedProblem = File.createTempFile("problem", ".hddl")

    formatConverterRonToOurs.processDomain(translatedDomain.getAbsolutePath, domainString)
    formatConverterRonToOurs.processProblem(translatedProblem.getAbsolutePath, problemString)

    val dom = scala.io.Source.fromFile(translatedDomain.getAbsolutePath).mkString
    val prob = scala.io.Source.fromFile(translatedProblem.getAbsolutePath).mkString

    HDDLParser.parseDomainAndProblem(new FileInputStream(translatedDomain),new FileInputStream(translatedProblem))
  }
}
