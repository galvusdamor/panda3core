package de.uniulm.ki.panda3.symbolic.writer.xml

import java.io.{FileInputStream, File, PrintWriter}

import de.uniulm.ki.panda3.symbolic.compiler.ToPlainFormulaRepresentation
import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.parser.hddl.HDDLParser
import de.uniulm.ki.panda3.symbolic.parser.xml.XMLParser
import de.uniulm.ki.panda3.symbolic.writer.hpddl.HPDDLWriter
import org.scalatest.FlatSpec

import scala.io.Source
import de.uniulm.ki.util._

/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
class XMLWriterTest extends FlatSpec {

  "Writing the parsed smartphone domain" must "yield a specific result" in {
    val domAlone: Domain = XMLParser.parseDomain(new FileInputStream("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/SmartPhone-HierarchicalNoAxioms.xml"))
    //val domAndInitialPlan: (Domain, Plan) = XMLParser.parseProblem("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/OrganizeMeeting_VerySmall.xml", domAlone)
    //val sortExpansion = domAndInitialPlan._1.expandSortHierarchy()

    //val parsedDom = domAndInitialPlan._1.update(sortExpansion)
    //val parsedProblem = domAndInitialPlan._2.update(sortExpansion)

    // apply the CWA
    //val cwaApplied = ClosedWorldAssumption.transform(parsedDom, parsedProblem, ())


    val dom = new XMLWriter("Smartphone", "Smartphone_Prob").writeDomain(domAlone)
    //val prob = HPDDLWriter("smartphone", "smartphone_verysmall").writeProblem(cwaApplied._1, cwaApplied._2)

    val correctDomain: String = Source.fromFile("src/test/resources/de/uniulm/ki/panda3/symbolic/writer/xml/smartphone_written.xml").mkString
    //val correctProblem: String = Source.fromFile("src/test/resources/de/uniulm/ki/panda3/symbolic/writer/hpddl/smartphone_verysmall.hpddl").mkString

    if (correctDomain != dom) writeStringToFile(dom, new File("/home/gregor/domsmart"))
    //if (correctProblem != prob) writeStringToFile(prob, new File("/home/gregor/prob"))

    assert(correctDomain == dom)
    //assert(correctProblem == prob)
  }

  "Writing the parsed UMTranslog" must "yield a specific result" in {
    val domAlone: Domain = XMLParser.parseDomain(new FileInputStream("src/test/resources/de/uniulm/ki/panda3/symbolic/writer/hpddl/UMTranslog.xml"))
    //val domAndInitialPlan: (Domain, Plan) = XMLParser.parseProblem("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/OrganizeMeeting_VerySmall.xml", domAlone)
    //val sortExpansion = domAndInitialPlan._1.expandSortHierarchy()

    //val parsedDom = domAndInitialPlan._1.update(sortExpansion)
    //val parsedProblem = domAndInitialPlan._2.update(sortExpansion)

    // apply the CWA
    //val cwaApplied = ClosedWorldAssumption.transform(parsedDom, parsedProblem, ())


    val dom = new XMLWriter("umtranslog", "umtranslog_prob").writeDomain(domAlone)
    //val prob = HPDDLWriter("smartphone", "smartphone_verysmall").writeProblem(cwaApplied._1, cwaApplied._2)

    val correctDomain: String = Source.fromFile("src/test/resources/de/uniulm/ki/panda3/symbolic/writer/xml/umtranslog_written.xml").mkString
    //val correctProblem: String = Source.fromFile("src/test/resources/de/uniulm/ki/panda3/symbolic/writer/hpddl/smartphone_verysmall.hpddl").mkString

    if (correctDomain != dom) writeStringToFile(dom, new File("/home/gregor/domtrans"))
    //if (correctProblem != prob) writeStringToFile(prob, new File("/home/gregor/prob"))

    assert(correctDomain == dom)
    //assert(correctProblem == prob)
  }

  "Writing the Sample domain" must "be correct" in {
    val domainFile = new FileInputStream("src/test/resources/de/uniulm/ki/panda3/symbolic/writer/xml/simpleDomain.pddl")
    val problemFile = new FileInputStream("src/test/resources/de/uniulm/ki/panda3/symbolic/writer/xml/simpleProblem.pddl")
    val parsedDomainAndProblem = HDDLParser.parseDomainAndProblem(domainFile, problemFile)

    val (dom,prob) = ToPlainFormulaRepresentation transform parsedDomainAndProblem

    val writer = new XMLWriter("simple", "simple_prob")

    val domWritten = writer.writeDomain(dom)
    val probWritten = writer.writeProblem(dom, prob)


    val correctDomain: String = Source.fromFile("src/test/resources/de/uniulm/ki/panda3/symbolic/writer/xml/simpleDomain.xml").mkString
    val correctProblem: String = Source.fromFile("src/test/resources/de/uniulm/ki/panda3/symbolic/writer/xml/simpleProblem.xml").mkString

    assert(correctDomain == domWritten)
    assert(correctProblem == probWritten)
  }

}
