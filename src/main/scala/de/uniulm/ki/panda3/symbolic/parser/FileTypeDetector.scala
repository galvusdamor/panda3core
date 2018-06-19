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

package de.uniulm.ki.panda3.symbolic.parser

import java.io.{ByteArrayInputStream, File, InputStream}
import java.util
import javax.xml.bind.{Unmarshaller, JAXBContext}
import javax.xml.parsers.SAXParserFactory
import javax.xml.transform.sax.SAXSource

import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.parser.hddl.{HDDLParser, hddlPanda3Visitor, antlrHDDLParser, antlrHDDLLexer}
import de.uniulm.ki.panda3.symbolic.parser.hpddl.HPDDLParser
import de.uniulm.ki.panda3.symbolic.parser.xml.{XMLParser, XMLDomain}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import org.antlr.v4.runtime
import org.antlr.v4.runtime._
import org.antlr.v4.runtime.atn.ATNConfigSet
import org.antlr.v4.runtime.dfa.DFA
import org.xml.sax.{InputSource, EntityResolver, XMLReader}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class FileTypeDetector(reportingFunction : String => Unit) extends Parser {


  private def tryToParseAsXML(domainString: String): Boolean = {
    val context: JAXBContext = JAXBContext.newInstance(classOf[XMLDomain])
    val marshaller: Unmarshaller = context.createUnmarshaller()
    val spf: SAXParserFactory = SAXParserFactory.newInstance()
    spf.setXIncludeAware(true)
    spf.setNamespaceAware(true)
    spf.setValidating(true)
    // Not required for JAXB/XInclude
    val xr: XMLReader = spf.newSAXParser().getXMLReader
    xr.setEntityResolver(new EntityResolver {
      override def resolveEntity(s: String, s1: String): InputSource = new InputSource(XMLParser.getClass.getResourceAsStream("domain-2.0.dtd"))
    })
    val source: SAXSource = new SAXSource(xr, new InputSource(new ByteArrayInputStream(domainString.getBytes())))
    try {
      marshaller.unmarshal(source).asInstanceOf[XMLDomain]
      true
    } catch {
      case _: Throwable => false
    }
  }

  private def tryToParseAsHDDL(domainString: String, problemString: String): Boolean = {
    val lDomain: antlrHDDLLexer = new antlrHDDLLexer(new ANTLRInputStream(new ByteArrayInputStream(domainString.getBytes())))
    val lProblem: antlrHDDLLexer = new antlrHDDLLexer(new ANTLRInputStream(new ByteArrayInputStream(problemString.getBytes())))
    val pDomain: antlrHDDLParser = new antlrHDDLParser(new CommonTokenStream(lDomain))
    val pProblem: antlrHDDLParser = new antlrHDDLParser(new CommonTokenStream(lProblem))

    var parseError = false
    val errorListener = new ANTLRErrorListener {
      override def reportContextSensitivity(recognizer: runtime.Parser, dfa: DFA, startIndex: Int, stopIndex: Int, prediction: Int,
                                            configs: ATNConfigSet): Unit = ()

      override def reportAmbiguity(recognizer: runtime.Parser, dfa: DFA, startIndex: Int, stopIndex: Int, exact: Boolean, ambigAlts: util.BitSet, configs: ATNConfigSet): Unit = ()

      override def reportAttemptingFullContext(recognizer: runtime.Parser, dfa: DFA, startIndex: Int, stopIndex: Int, conflictingAlts: util.BitSet, configs: ATNConfigSet): Unit = ()

      override def syntaxError(recognizer: Recognizer[_, _], offendingSymbol: scala.Any, line: Int, charPositionInLine: Int, msg: String, e: RecognitionException): Unit =
        parseError = true

    }


    pDomain.removeErrorListeners()
    pDomain.addErrorListener(errorListener)
    pProblem.removeErrorListeners()
    pProblem.addErrorListener(errorListener)

    // run the parser to detect errors, but tell him not to do out
    new hddlPanda3Visitor(false).visitInstance(pDomain.domain, pProblem.problem)

    !parseError
  }


  override def parseDomainAndProblem(domainFile: InputStream, problemFile: InputStream): (Domain, Plan) = {
    // first we get both the domain and the problem
    val domainString = scala.io.Source.fromInputStream(domainFile).mkString
    val problemString = scala.io.Source.fromInputStream(problemFile).mkString

    if (tryToParseAsXML(domainString)) {
      reportingFunction("using XML parser ... ")
      XMLParser.asParser.parseDomainAndProblem(new ByteArrayInputStream(domainString.getBytes()), new ByteArrayInputStream(problemString.getBytes()))
    } else if (tryToParseAsHDDL(domainString, problemString)) {
      reportingFunction("using HDDL parser ... ")
      HDDLParser.parseDomainAndProblem(new ByteArrayInputStream(domainString.getBytes()), new ByteArrayInputStream(problemString.getBytes()))
    } else {
      reportingFunction("using HPDDL parser ... ")
      HPDDLParser.parseDomainAndProblem(new ByteArrayInputStream(domainString.getBytes()), new ByteArrayInputStream(problemString.getBytes()))
    }
  }
}
