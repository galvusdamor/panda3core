package de.uniulm.ki.panda3.symbolic.writer.hpddl

import java.io.PrintWriter

import de.uniulm.ki.panda3.symbolic.compiler.ClosedWorldAssumption
import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.parser.xml.XMLParser
import de.uniulm.ki.panda3.symbolic.plan.Plan
import org.scalatest.FlatSpec

import scala.io.Source

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class HPDDLWriterTest extends FlatSpec {

  "Writing the parsed smartphone domain" must "yield a specific result" in {
    val domAlone: Domain = XMLParser.parseDomain("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/SmartPhone-HierarchicalNoAxioms.xml")
    val domAndInitialPlan: (Domain, Plan) = XMLParser.parseProblem("src/test/resources/de/uniulm/ki/panda3/symbolic/parser/OrganizeMeeting_VerySmall.xml", domAlone)
    val sortExpansion = domAndInitialPlan._1.expandSortHierarchy()

    val parsedDom = domAndInitialPlan._1.update(sortExpansion)
    val parsedProblem = domAndInitialPlan._2.update(sortExpansion)

    // apply the CWA
    val cwaApplied = ClosedWorldAssumption.transform(parsedDom, parsedProblem, ())


    val dom = HPDDLWriter("smartphone", "smartphone_verysmall").writeDomain(domAlone)
    val prob = HPDDLWriter("smartphone", "smartphone_verysmall").writeProblem(cwaApplied._1, cwaApplied._2)

    val correctDomain: String = Source.fromFile("src/test/resources/de/uniulm/ki/panda3/symbolic/writer/hpddl/smartphone.hpddl").mkString
    val correctProblem: String = Source.fromFile("src/test/resources/de/uniulm/ki/panda3/symbolic/writer/hpddl/smartphone_verysmall.hpddl").mkString

    if (correctProblem != prob) {
      Some(new PrintWriter("src/test/resources/de/uniulm/ki/panda3/symbolic/writer/hpddl/smartphone_diff.hpddl")).foreach { p => p.write(prob); p.close() }
    }

    assert(correctDomain == dom)
    assert(correctProblem == prob)
  }

}
