package de.uniulm.ki.panda3.symbolic.writer.xml

import java.io.{File, PrintWriter}

import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.parser.xml.XMLParser
import org.scalatest.FlatSpec

import scala.io.Source

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class XMLWriterTest extends FlatSpec {

  def writeStringToFile(s: String, file: File): Unit = {
    Some(new PrintWriter(file)).foreach { p => p.write(s); p.close() }
  }

  "Writing the parsed smartphone domain" must "yield a specific result" in {
    val domAlone: Domain = XMLParser.parseDomain("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/SmartPhone-HierarchicalNoAxioms.xml")
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
    val domAlone: Domain = XMLParser.parseDomain("src/test/resources/de/uniulm/ki/panda3/symbolic/writer/hpddl/UMTranslog.xml")
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

}
